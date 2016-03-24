package sd.tp1.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 24/03/2016.
 */
public class DeleteAlbumREST {

    public static void deleteAlbum(WebTarget target, String albumName) {
        target.path("/albums/"+albumName)
                .request()
                .delete();
    }
}
