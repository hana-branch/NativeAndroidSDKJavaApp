//package remoteInterface;
//
//
//import android.util.Log;
//
//import org.json.JSONObject;
//
//import java.net.SocketTimeoutException;
//import java.util.concurrent.TimeUnit;
//
//import io.branch.referral.BranchError;
//import io.branch.referral.network.BranchRemoteInterface;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//public class OkhttpBranchNetworkInterface extends BranchRemoteInterface {
//    private OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS).writeTimeout(3, TimeUnit.SECONDS).connectTimeout(3, TimeUnit.SECONDS).build();
//
//    @Override
//    public BranchResponse doRestfulGet(String url) throws BranchRemoteException {
//        Log.e("BranchSDK doRestfulGet URL : ", url);
//        Request request = new Request.Builder().url(url).build();
//
//        return handleNetworkRequest(request);
//    }
//
//    @Override
//    public BranchResponse doRestfulPost(String url, JSONObject payload) throws BranchRemoteException {
//        Log.e("BranchSDK doRestfulPost URL : ", url);
//        Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), payload.toString())).build();
//
//        return handleNetworkRequest(request);
//    }
//
//    private BranchResponse handleNetworkRequest(Request request) throws BranchRemoteException {
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            ResponseBody rb = response.body();
//            if (rb != null) throw new BranchRemoteException(BranchError.ERR_BRANCH_NO_CONNECTIVITY);
//            return new BranchResponse(rb.string(), response.code());
//        } catch(Exception exception) {
//            if (exception instanceof SocketTimeoutException) {
//                // add desired retry logic, then eventually throw BranchError.ERR_BRANCH_REQ_TIMED_OUT
//                throw new BranchRemoteException(BranchError.ERR_BRANCH_REQ_TIMED_OUT);
//            } else {
//                // handle other failures as needed, throw BranchError.ERR_BRANCH_NO_CONNECTIVITY as a default
//                throw new BranchRemoteException(BranchError.ERR_BRANCH_NO_CONNECTIVITY);
//            }
//        }
//    }
//}