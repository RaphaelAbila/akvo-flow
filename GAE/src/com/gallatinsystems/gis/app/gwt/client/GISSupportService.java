package com.gallatinsystems.gis.app.gwt.client;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gissupportrpcservice")
public interface GISSupportService extends RemoteService {
	TreeMap<String, String> listCountryCodes();

	TreeMap<String, String> listCoordinateTypes();

	ArrayList<Integer> listUTMZones();

	TreeMap<String, String> listFeatureTypes();
}