package sd.tp1.server;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.codec.binary.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


/**
 * Created by Antonio on 10/05/16.
 */
@Path("/albums")
public class AlbumsProxyResource {

    private OAuth20Service service;
    private OAuth2AccessToken accessToken;
    private String srvpass;
    private Map<String,String> albumsIdName;
    private List<ImgurPicture> pictures;
    private Map<String,Long> pictureSize;
    private long serversize;
    public AlbumsProxyResource(OAuth20Service service, OAuth2AccessToken accessToken,String srvpass){
        this.service = service;
        this.accessToken = accessToken;
        this.srvpass= srvpass;
        albumsIdName = new HashMap<>();
        pictures = new LinkedList<>();
        pictureSize=new HashMap<>();
        serversize=0;
    }

    private boolean checkPassword(String srvpass){
        return this.srvpass.equals(srvpass);
    }

    @GET
    @Path("/serverBytes/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getserverSpace(@PathParam("password") String password) {
        if (checkPassword(password)){
            return Response.ok(serversize).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumList(@PathParam("password") String password) {

        if (checkPassword(password)) {
            String imgurUrl = "https://api.imgur.com/3/account/me/albums";
            try {
                List<String> albumsTitleList = new LinkedList<>();
                OAuthRequest albumsReq = new OAuthRequest(Verb.GET, imgurUrl, service);
                service.signRequest(accessToken, albumsReq);
                final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
                if (albumsRes.getCode() == 200) {

                    JSONParser parser = new JSONParser();

                    JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
                    JSONArray images = (JSONArray) res.get("data");

                    Iterator albumsIt = images.iterator();
                    albumsIdName.clear();

                    while(albumsIt.hasNext()){

                        JSONObject objects = (JSONObject) albumsIt.next();
                        String title = objects.get("title").toString();
                        String albumId= objects.get("id").toString();
                        albumsTitleList.add(title);
                        albumsIdName.put(albumId,title);
                    }
                }
                albumsIdName.toString();
                if (albumsTitleList.size()>0) {

                    return Response.ok(albumsTitleList).build();
                }

                return  Response.status(Response.Status.NOT_FOUND).build();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @GET
    @Path("/{albumName}/key/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListPicturesAt(@PathParam("albumName") String albumName, @PathParam("password") String password) {
        List<String> picturesList = new LinkedList<>();

        System.out.println("OROROR");
        if (checkPassword(password)) {
            String albumID;
            if ((albumID = albumName2Id(albumName))!=null){
                String albumUrl = "https://api.imgur.com/3/album/"+albumID+"/images";
                try{
                    OAuthRequest albumReq = new OAuthRequest(Verb.GET,albumUrl,service);
                    service.signRequest(accessToken,albumReq);
                    final com.github.scribejava.core.model.Response albumPRes = albumReq.send();
                    System.out.println("Vai entrar");
                    if (albumPRes.getCode() == 200){
                        System.out.println("ENTROU AQUI");
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());
                        JSONArray images = (JSONArray) res.get("data");

                        Iterator albumsIt = images.iterator();
                        while (albumsIt.hasNext()){

                            JSONObject objects = (JSONObject) albumsIt.next();
                            String title = (String)objects.get("title");
                            String pictureId= (String)objects.get("id");
                            picturesList.add(title);

                            ImgurPicture iP = new ImgurPicture(pictureId,title,albumID);

                        }

                            return Response.ok(picturesList).build();

                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }



    @GET
    @Path("/{albumName}/{picture}/key/{password}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPictureData(@PathParam("albumName") String albumName, @PathParam("picture") String pictureName,@PathParam("password") String password){

        System.out.println("my pw " + srvpass + " enter pass : " +password);

        if (checkPassword(password)) {
            System.out.println(albumsIdName.toString() + " " + albumsIdName.get(albumName) != null);
            String albumID;
            if ((albumID=albumName2Id(albumName)) != null) {
                ImgurPicture iP;
                System.out.println(pictures.size() + " " +(iP=getPictureWithName(pictureName))!=null);
                if ((iP=getPictureWithName(pictureName))!=null) {
                    try {
                        System.out.println(albumsIdName.toString());
                        System.out.println("PIC ID " + iP.getId());
                        String imageUrl = "https://api.imgur.com/3/album/" + albumID + "/image/" + iP.getId();

                        OAuthRequest albumReq = new OAuthRequest(Verb.GET,imageUrl,service);
                        service.signRequest(accessToken,albumReq);
                        final com.github.scribejava.core.model.Response albumPRes = albumReq.send();

                        System.out.println("CODIGO DA RES GETPICTUREDATA " + albumPRes.getCode() );
                        if (albumPRes.getCode() == 200) {
                            JSONParser parser = new JSONParser();
                            JSONObject res = (JSONObject) parser.parse(albumPRes.getBody());

                            String downloadlink = (String)((JSONObject)res.get("data")).get("link");

                            URL url = new URL(downloadlink);

                            InputStream inputStream = new BufferedInputStream(url.openStream());

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            int n = 0;
                            while (-1 != (n = inputStream.read(buf))) {
                                out.write(buf, 0, n);
                            }
                            out.close();
                            inputStream.close();
                            return Response.ok(out.toByteArray()).build();
                        }

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Path("/key/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAlbum(String albumName,@PathParam("password") String password){
        if(checkPassword(password)){
                try{
                    String creationUrl = "https://api.imgur.com/3/album";
                    OAuthRequest albumReq = new OAuthRequest(Verb.POST,creationUrl,service);
                    albumReq.addParameter("title",albumName);
                    service.signRequest(accessToken,albumReq);

                    final com.github.scribejava.core.model.Response albumRes = albumReq.send();

                    if(albumRes.getCode()==200){
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(albumRes.getBody());

                        String albumID = (String)((JSONObject)res.get("data")).get("id");

                        albumsIdName.put(albumID,albumName);

                        return Response.ok().build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).build();

                }catch (Exception e){
                    e.printStackTrace();
                }

        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }


    @POST
    @Path("/{albumName}/{pictureName}/key/{password}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadPicture(@PathParam("albumName") String albumName,@PathParam("pictureName")String pictureName,byte[] pictureData,@PathParam("password")String password){
        if (checkPassword(password)){
            try{
                boolean hasAlbum = false;
                String albumID="";
                for (String key : albumsIdName.keySet()){
                    if(albumsIdName.get(key).equals(albumName)){
                        hasAlbum = true;
                        albumID=key;
                    }
                }
                if(hasAlbum) {
                    String upImageUrl = "https://api.imgur.com/3/image";
                    OAuthRequest upImageReq = new OAuthRequest(Verb.POST, upImageUrl, service);
                    upImageReq.addParameter("image", org.apache.commons.codec.binary.Base64.encodeBase64String(pictureData));
                    upImageReq.addParameter("title", pictureName);
                    upImageReq.addParameter("album", albumID);
                    service.signRequest(accessToken, upImageReq);

                    final com.github.scribejava.core.model.Response upImageRes = upImageReq.send();

                    if(upImageRes.getCode()==200){
                        JSONParser parser = new JSONParser();
                        JSONObject res = (JSONObject) parser.parse(upImageRes.getBody());

                        String imageID = (String)((JSONObject)res.get("data")).get("id");

                        pictures.add(new ImgurPicture(imageID,pictureName,albumID));
                        serversize+=pictureData.length;
                        pictureSize.put(imageID,(long)pictureData.length);
                        return Response.ok().build();
                    }
                }

                return Response.status(Response.Status.NOT_FOUND).build();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    @DELETE
    @Path("/{albumName}/key/{password}")
    public Response deleteAlbum(@PathParam("albumName") String albumName,@PathParam("password") String password){
        if (checkPassword(password)) {
            String albumID;
            if((albumID = albumName2Id(albumName))!=null) {
                String dAlbUrl = "https://api.imgur.com/3/album/"+albumID;
                OAuthRequest dAlbReq = new OAuthRequest(Verb.DELETE, dAlbUrl, service);
                service.signRequest(accessToken, dAlbReq);

                final com.github.scribejava.core.model.Response dAlbRes = dAlbReq.send();

                albumsIdName.remove(albumID);

                return  Response.status(dAlbRes.getCode()).build();
            }else return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }



    @DELETE
    @Path("/{albumName}/{pictureName}/key/{password}")
    public Response deletePicture(@PathParam("albumName") String albumName, @PathParam("pictureName")String pictureName,@PathParam("password")String password){

        if(checkPassword(password)){
            String albumID;
            if((albumID = albumName2Id(albumName)) !=null){
                String pic = "";
                for ( ImgurPicture picture : pictures) {
                    if(picture.getPicName().equals(pictureName) && picture.getAlbumId().equals(albumID)){
                        pic = picture.getId();
                        pictures.remove(picture);
                        serversize-=pictureSize.remove(pic);
                        break;
                    }
                }

                if(!pic.equals("")) {
                    String dPicUrl = "https://api.imgur.com/3/image/" + pic;
                    OAuthRequest dPicReq = new OAuthRequest(Verb.DELETE, dPicUrl, service);
                    service.signRequest(accessToken, dPicReq);

                    final com.github.scribejava.core.model.Response dPicRes = dPicReq.send();


                    return Response.status(dPicRes.getCode()).build();
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    private String albumName2Id(String name){
        for (String key : albumsIdName.keySet()){
            if (albumsIdName.get(key).equals(name)){
                return key;
            }
        }
        return null;
    }


    private ImgurPicture getPictureWithName(String name){

        for (ImgurPicture iP: pictures) {
            if(iP.getPicName().equals(name)){
                return iP;
            }
        }
        return null;
    }


}
