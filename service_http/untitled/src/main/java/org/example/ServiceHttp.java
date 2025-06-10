package org.example;

import java.net.http.HttpResponse;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHttp extends Remote {

    HttpResponse<String> fetchAPI(String url) throws RemoteException;

}
