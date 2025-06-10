package org.example;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHttp extends Remote {

    void init(String adresseProxy, int portProxy) throws RemoteException;

    void init() throws RemoteException;

    HttpResponse<String> fetchAPI(String url) throws RemoteException;

}
