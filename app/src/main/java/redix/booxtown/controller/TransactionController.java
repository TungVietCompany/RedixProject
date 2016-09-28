package redix.booxtown.controller;

import android.os.StrictMode;

import java.util.Hashtable;

import redix.booxtown.api.ServiceGenerator;
import redix.booxtown.api.ServiceInterface;
import redix.booxtown.model.Result;
import retrofit2.Call;

/**
 * Created by Administrator on 28/09/2016.
 */
public class TransactionController {
    private ServiceInterface service;
    Boolean success;
    String result;
    public TransactionController(){
        service = ServiceGenerator.GetInstance();
    }

    public String transactionInsert(String buyUserID, String sellUserID, String buyBookID, String sellBookID,String action, String session_id){
        Hashtable obj= new Hashtable();
        obj.put("session_id",session_id);
        obj.put("buyUserID",buyUserID);
        obj.put("sellUserID",sellUserID);
        obj.put("buyBookID",buyBookID);
        obj.put("sellBookID",sellBookID);
        obj.put("action",action);
        Call<Result> transactionInsert = service.transactionInsert(obj);
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Result str = transactionInsert.execute().body();
            if (str.getCode() == 200){
                result= str.getSession_id();
            }
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }
}
