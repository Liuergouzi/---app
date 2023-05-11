package com.hjq.demo.comfirm;


import com.aliyun.dypnsapi20170525.models.VerifyMobileRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;


public class MockRequest {
    private static final String TAG = MockRequest.class.getSimpleName();


    public static String verifyNumber(String token, String phoneNumber) {
        com.aliyun.dypnsapi20170525.Client client = null;
        try {
            client = MockRequest.createClient("", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "linkerror";
        }
        VerifyMobileRequest verifyMobileRequest = new VerifyMobileRequest().setPhoneNumber(phoneNumber).setAccessCode(token);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            String verifyresult=client.verifyMobileWithOptions(verifyMobileRequest, runtime).getBody().getGateVerifyResultDTO().verifyResult;
            if (verifyresult!=null){
                return verifyresult;
            }
        } catch (TeaException error) {
            System.out.println("aaaaaaaaaaaaaaaaaaaa"+error.getCode());
                return "neterror";
        } catch (Exception _error) {
                return "phoneerror";
        }
        return "null";
    }

    public static com.aliyun.dypnsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dypnsapi.aliyuncs.com";
        return new com.aliyun.dypnsapi20170525.Client(config);
    }

}
