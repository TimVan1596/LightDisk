package model;

/**
 * <h3>BlockChain</h3>
 * <p>接口返回值response统一标准格式</p>
 *
 * @author : TimVan
 * @date : 2020-04-01 17:49
 **/
public class ResponseJson {
    /** SUCCESSFUL_CODE = 请求成功
     *  FAIL_CODE = 默认请求失败
     * */
    private final static int SUCCESSFUL_CODE = 0;
    private final static int FAIL_CODE = 1;


    /**
     * code = 状态码
     * */
    private int code;
    private String msg;
    private Object data;

    public ResponseJson(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseJson() {
        this.code = FAIL_CODE;
        this.msg = "";
        this.data = null;
    }

    public int getCode() {
        return code;
    }

    public void setCodeSuccessful() {
        this.code = SUCCESSFUL_CODE;
    }

    public void setCodeFailed() {
        this.code = FAIL_CODE;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
