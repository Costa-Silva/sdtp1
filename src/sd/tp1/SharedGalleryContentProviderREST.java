package sd.tp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sd.tp1.client.*;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

import javax.ws.rs.client.WebTarget;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProviderREST implements GalleryContentProvider{

	Gui gui;	
	WebTarget target;
	SharedGalleryContentProviderREST() {
		// TODO: code to do when shared gallery starts
		target= ClientDiscoveryREST.getWebTarget();
	}

	
	/**
	 *  Downcall from the GUI to register itself, so that it can be updated via upcalls.
	 */
	@Override
	public void register(Gui gui) {
		if( this.gui == null ) {
			this.gui = gui;
			new Thread(()->{
				for(;;) {
					List<Album> l = getListOfAlbums();
					if (l != null){
						if (!l.isEmpty()) {
							Iterator<Album> it = l.iterator();
							while (it.hasNext()) {
								gui.updateAlbum(it.next());
							}
							gui.updateAlbums();
						}
					}
					try {
						Thread.sleep(4000);
					}catch (Exception e) {
					}
				}

			}).start();
		}
	}

	/**
	 * Returns the list of albums in the system.
	 * On error this method should return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {
		// TODO: obtain remote information

		List<Album> list = new ArrayList<Album>();

		for (String album: GetAlbumListREST.getAlbumList(target)) {
			list.add(new SharedAlbum(album));
		}

		return list;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		// TODO: obtain remote information 
		List<Picture> list = new ArrayList<Picture>();

		for (String albumName: GetPicturesListREST.getPicturesList(target,album.getName())) {
			list.add(new SharedPicture(albumName));
		}
		return list;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		// TODO: obtain remote information 
		byte[] aux;
			if((aux = GetPictureDataREST.getPictureData(target,album.getName(), picture.getName()))!=null){
				return aux;
		}
		return null;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		// TODO: contact servers to create album
		String nome;

			if ((nome = CreateAlbumREST.createAlbum(target, name)) != null) {
				return new SharedAlbum(nome);
				}
		return null;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		// TODO: contact servers to delete album
		DeleteAlbumREST.deleteAlbum(target,album.getName());
	}
	
	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		// TODO: contact servers to add picture name with contents data 
		return new SharedPicture(name);
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		// TODO: contact servers to delete picture from album 
		return true;
	}

	
	/**
	 * Represents a shared album.
	 */
	static class SharedAlbum implements GalleryContentProvider.Album {
		final String name;

		SharedAlbum(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	/**
	 * Represents a shared picture.
	 */
	static class SharedPicture implements GalleryContentProvider.Picture {
		final String name;

		SharedPicture(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
