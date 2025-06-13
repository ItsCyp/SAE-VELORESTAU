import java.io.Serializable;

//classe utilisée pour transférer les réponses d'api. 
//La classe HttpResponse ne semble pas
//vouloir s'envoyer en rmi, et la longueur de la chaîne 
//est différente de la taille du message http
public class DataTransfer implements Serializable {

    private String data;
    private int dataLength;

    public DataTransfer(int len, String data) {
        this.data = data;
        this.dataLength = len;
    }

    public String getData() {
        return this.data;
    }

    public int getDataLength() {
        return this.dataLength;
    }

    public void setData(int len, String data) {
        this.data = data;
        this.dataLength = len;
    }
}
