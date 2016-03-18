
package sd.tp1.client.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Server", targetNamespace = "http://server.tp1.sd/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Server {


    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAlbumList", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetAlbumList")
    @ResponseWrapper(localName = "getAlbumListResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetAlbumListResponse")
    @Action(input = "http://server.tp1.sd/Server/getAlbumListRequest", output = "http://server.tp1.sd/Server/getAlbumListResponse")
    public List<String> getAlbumList();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPicturesList", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPicturesList")
    @ResponseWrapper(localName = "getPicturesListResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPicturesListResponse")
    @Action(input = "http://server.tp1.sd/Server/getPicturesListRequest", output = "http://server.tp1.sd/Server/getPicturesListResponse")
    public List<String> getPicturesList(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns sd.tp1.client.ws.FileInfo
     * @throws InfoNotFoundException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getFileInfo", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetFileInfo")
    @ResponseWrapper(localName = "getFileInfoResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetFileInfoResponse")
    @Action(input = "http://server.tp1.sd/Server/getFileInfoRequest", output = "http://server.tp1.sd/Server/getFileInfoResponse", fault = {
        @FaultAction(className = InfoNotFoundException_Exception.class, value = "http://server.tp1.sd/Server/getFileInfo/Fault/InfoNotFoundException")
    })
    public FileInfo getFileInfo(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws InfoNotFoundException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns byte[]
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPictureData", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPictureData")
    @ResponseWrapper(localName = "getPictureDataResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPictureDataResponse")
    @Action(input = "http://server.tp1.sd/Server/getPictureDataRequest", output = "http://server.tp1.sd/Server/getPictureDataResponse")
    public byte[] getPictureData(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1);

}
