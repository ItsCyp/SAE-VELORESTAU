package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Remote service used to retrieve the body of an HTTP request.
 */
public interface ServiceHttp extends Remote {

    /**
     * Fetches the content of the given URL.
     *
     * @param url the address to request
     * @return the response body as a string
     * @throws RemoteException if a remote communication error occurs
     */
    String fetchAPI(String url) throws RemoteException;

}
