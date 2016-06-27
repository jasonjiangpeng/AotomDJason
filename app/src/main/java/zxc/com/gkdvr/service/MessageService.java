//package zxc.com.gkdvr.service;
//
//import android.app.Activity;
//import android.app.Service;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.text.NumberFormat;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import zxc.com.gkdvr.utils.Constance;
//import zxc.com.gkdvr.utils.FileAccessor;
//import zxc.com.gkdvr.utils.WifiAdmin;
//
//public class MessageService extends Service {
//    public final static String TAG = "MessageService";
//    public final static String RECORD_FILE_THUMBNAIL = FileAccessor.IMESSAGE_VIDEO + "/thumbnail";
//    public final static String PROTECT_FILE_THUMBNAIL = FileAccessor.IMESSAGE_PROTECT + "/thumbnail";
////    public final static String PICTURE_FILE_TEMP = Constance.download_path + "/tmp/";
//
//    private final int NOTIFY_ID = 0x01;
//    private final int gl_progressMax = 100;
//
//    private int gl_progress = 0;
//    private boolean gl_indeterminate = false;
//
//    private String filename = "";
//    private int filetype = 0;
//    private int gl_file_size = 0;
//    private int gl_recv_size = 0;
//
//    private int gl_record_file_size = 0;
//    private int gl_record_recv_size = 0;
//
//    private boolean isRunning = false;
//    private boolean isConnected = false;
//    private boolean isFirst = true;
//    private boolean isGetList = false;
//    private boolean isGetListFirst = false;
//    private static Socket socket;
//    private ServiceBinder mBinder = new ServiceBinder();
//    private Thread recv;
//    private Lock lock = new ReentrantLock();
//    private Lock listlock = new ReentrantLock();
//    private boolean gl_isDownloadcontinue = false;
//    Handler gl_message_handler;
//    Activity rtspClientActivity;
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        gl_isDownloadcontinue = false;
//        if (isFirst) {
//            Thread t = new Thread() {
//                public void run() {
//                    connect("192.168.99.1", 8080);
//
//                }
//            };
//            t.start();
//            isFirst = false;
//        }
//    }
//
//    public void setActivity(Activity rtspClient) {
//        this.rtspClientActivity = rtspClient;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        isRunning = false;
//
//        WaitForThreadStop(recv);
//        close();
//    }
//
//    public boolean connect(String ip, int port) {
//        try {
//            Log.d("Allen", "connect111 message service socket connect");
//            InetAddress targetAddr = InetAddress.getByName(ip);
//            socket = null;
//            socket = new Socket(targetAddr, port);
//            socket.setTcpNoDelay(true);
////			socket.setSoTimeout(10000);
//            isRunning = true;
//            isConnected = true;
//            receive();
//            Intent i = new Intent(Constance.BROADCAST_SOCKET_CONNECT);
//            sendBroadcast(i);
//            return true;
//        } catch (UnknownHostException e) {
//
//            e.printStackTrace();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//
//
//        return false;
//    }
//
//    public boolean CheckSocketIsAlive() {
//
//        if (socket.isConnected()) {
//            if (isConnected) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//
//    public boolean send(String data) {
//
//        if (!isSocketAlive()) {
//            if (!data.contains("streamvideo")) {
//                Log.d("Allen", "socket reconnect");
//                reconnect();
//            }
//            return false;
//        }
//
//
//        lock.lock();
//        int count = 0;
//        boolean result = false;
//
//        do {
//            if (socket != null) {
//
//                try {
//                    Log.d("Sonix", "Send Socket to Device: " + data);
//
//
//                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//
//                    out.println(data);
//
//                    if (recv.isAlive()) {
//
//                    } else {
//
//                    }
//                    result = true;
//                    break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//
//                }
//
//            } else {
//                isRunning = false;
//
//
//            }
//            count++;
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } while (count < 3);
//        lock.unlock();
//        return result;
//    }
//
//    public boolean send(byte[] data) {
//        lock.lock();
//        int count = 0;
//        boolean result = false;
//
//        do {
//            if (socket != null) {
//                try {
//
//                    Log.d("Sonix", "Send Socket to Device: " + new String(data));
//
//                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//                    out.println(data);
//
//                    if (recv.isAlive()) {
//
//                    } else {
//
//                    }
//                    result = true;
//                    break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                isRunning = false;
//                connect("192.168.99.1", 8080);
//
//            }
//            count++;
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } while (count < 3);
//        lock.unlock();
//        return false;
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        // TODO Auto-generated method stub
//        super.onTaskRemoved(rootIntent);
//        try {
//            rtspClientActivity.stopJniLive();
//            WifiAdmin wifiAdmin = new WifiAdmin(MessageService.this);
//            wifiAdmin.disconnectWifi();
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//    }
//
//    private int percent(int diliverNum, int queryMailNum) {
//        int result = 0;
//
//        NumberFormat numberFormat = NumberFormat.getInstance();
//
//        numberFormat.setMaximumFractionDigits(0);
//        String tmpResult = numberFormat.format((float) diliverNum / (float) queryMailNum * 100);
//        result = Integer.valueOf(tmpResult);
//
//        if (result >= 100) {
//            if (diliverNum >= queryMailNum) {
//                result = 100;
//            } else {
//                result = 99;
//            }
//        }
//        return result;
//    }
//
//
//    public boolean isSocketAlive() {
//
//        return isConnected;
//    }
//
//    public void reconnect() {
//        Thread t = new Thread() {
//            public void run() {
//                isRunning = false;
//                if (socket != null && isConnected/* socket.isConnected() */) {
//                    isConnected = false;
//                    try {
//                        WaitForThreadStop(recv);
//                        recv = null;
//
//
//                        socket.close();
//
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        Log.d("reconnect", "socket exception =" + e.toString());
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                        Log.d("reconnect", "socket exception =" + e.toString());
//                    }
//                }
//                socket = null;
//                connect("192.168.99.1", 8080);
//
//            }
//        };
//        t.start();
//    }
//
//    public void getList() {
//        isGetList = true;
//        isGetListFirst = true;
//    }
//
//    private boolean WaitForThreadStop(Thread t) {
//        int waitCount = 0;
//
//        if (t == null) {
//            return true;
//        }
//        t.interrupt();
//        while (waitCount < 2) {
//            if (t.isAlive() == false) {
//                return true;
//            }
//            try {
//                t.sleep(20);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            waitCount++;
//        }
//        if (t.isAlive() == true) {
//            try {
//                t.stop();
//                return true;
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//        }
//        return false;
//    }
//    //
//
//
//    //
//
//
//    private void receive() {
//        recv = new Thread() {
//            public void run() {
//                while (isRunning) {
//                    byte[] buffer = new byte[1024];
//                    //	byte[] buffer2=new byte[1024];
//                    try {
//                        if (socket == null) {
//                            return;
//                        }
//                        InputStream input = socket.getInputStream();
//                        int num = 0;
//                        while ((num = input.read(buffer)) != -1 && isRunning == true) {
//                            final byte[] resp = new byte[num];
//                            System.arraycopy(buffer, 0, resp, 0, num);
//                            Log.d("Sonix", "Receive Socket from Device: " + new String(resp));
//                            try {
//                                JSONObject json = new JSONObject(new String(resp));
//                                String cmd_type = json.getString("type");
//
//                                if (cmd_type.equals("setchannel_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_CHANNEL);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setpwd_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_PWD);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setwdr_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_WDR);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setmirror_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_MIRROR);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setflip_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_FLIP);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getsdspace_res")) {
//                                    int available = json.getInt("sdspace");
//                                    int total = json.getInt("totalspace");
//                                    int errorcode = json.getInt("errorcode");
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_SD_SPACE);
//                                    intent.putExtra("available", available);
//                                    intent.putExtra("total", total);
//                                    intent.putExtra("errorcode", errorcode);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setsdformat_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_SD_FORMAT);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setrecordstatus_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_RECORD_STATUS);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("takepicture_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_TAKE_PICTURE);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("synctime_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SYNC_TIME);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("gettime_res")) {
//                                    int year = json.getInt("year");
//                                    int month = json.getInt("month");
//                                    int day = json.getInt("day");
//                                    int hour = json.getInt("hour");
//                                    int min = json.getInt("min");
//                                    int sec = json.getInt("sec");
//                                    String timezone = json.getString("timezone");
//                                } else if (cmd_type.equals("getbatterystatus_res")) {
//                                    int battery = json.getInt("batterylevel");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_BATTERY_STATUS);
//                                    intent.putExtra("battery", battery);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("downloadfile_res")) {
//                                    int status = json.getInt("status");
//                                    int tag = 0;
//                                    if (json.has("tag")) {
//                                        tag = json.getInt("tag");
//                                    }
//
//
//                                    Log.d("Allen", "downloadfile_res 00000 ");
//                                    if (status != MessageToast.DOWNLOAD_FILE_SUCCESS) {
//                                        //	String filename=json.getString("filename");
//
//                                        Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                                        intent.putExtra("status", MessageToast.DOWNLOAD_FILE_COMMAND_FAIL);
//                                        intent.putExtra("filename", filename);
//                                        intent.putExtra("tag", tag);
//                                        sendBroadcast(intent);
//                                        continue;
//                                    }
//                                    Log.d("Allen", "downloadfile_res 1111 ");
//                                    if (output != null) {
//                                        output.close();
//                                        output = null;
//                                    }
//                                    if (filetype == 0) {
//                                        File mFilePath = new File(FileAccessor.IMESSAGE_VIDEO);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        String filePath = getFileSaveName(FileAccessor.IMESSAGE_VIDEO +"/" +filename, ".avi");
//                                        output = new FileOutputStream(filePath, gl_isDownloadcontinue);
//                                    } else if (filetype == 1) {
//                                        File mFilePath = new File(FileAccessor.IMESSAGE_PROTECT);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        String filePath = getFileSaveName(FileAccessor.IMESSAGE_PROTECT+"/" + filename, ".avi");
//                                        output = new FileOutputStream(filePath, gl_isDownloadcontinue);
//                                    } else if (filetype == 2) {
//                                        File mFilePath = new File(FileAccessor.IMESSAGE_IMAGE);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        String filePath = getFileSaveName(FileAccessor.IMESSAGE_IMAGE + "/" + filename, ".jpg");
//                                        output = new FileOutputStream(filePath);
//                                    } else if (filetype == 3) {
//                                        File mFilePath = new File(FileAccessor.IMESSAGE_FILE);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        Log.d("Allen", "camwrite: " + mFilePath.canWrite());
//                                        Log.d("Allen", "downloadfile_res 22222 ");
//                                        String filePath = /*PICTURE_FILE_TEMP*/FileAccessor.IMESSAGE_FILE + "/" + filename;
//                                        Log.d("Allen", "downloadfile_res 3333 filePath =" + filePath);
//                                        output = new FileOutputStream(filePath);
//                                        Log.d("Allen", "downloadfile_res 44444 filePath =" + filePath);
//                                    } else if (filetype == 4) {
//                                        String filePath = FileAccessor.IMESSAGE_FILE ;
//                                        File mFilePath = new File(filePath);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        filePath = filePath + "/" + filename;
//                                        try {
//                                            output = new FileOutputStream(filePath);
//
//                                        } catch (Exception e) {
//                                            // TODO: handle exception
//                                            continue;
//                                        }
//
//                                    } else if (filetype == 5) {
//                                        File mFilePath = new File(RECORD_FILE_THUMBNAIL);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        String filePath = getFileSaveName(RECORD_FILE_THUMBNAIL + "/" + filename, ".jpg");
//                                        output = new FileOutputStream(filePath);
//                                    } else if (filetype == 6) {
//                                        File mFilePath = new File(PROTECT_FILE_THUMBNAIL);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        String filePath = getFileSaveName(PROTECT_FILE_THUMBNAIL + "/" + filename, ".jpg");
//                                        output = new FileOutputStream(filePath);
//                                    }
//                                    Log.d("Allen", "BROADCAST_DOWNLOAD_FILE 555 filetype" + filetype);
//
//                                    Log.d("Allen", "BROADCAST_DOWNLOAD_FILE 555 filename" + filename);
//                                    respDownloadFile(resp);
//                                } else if (cmd_type.equals("downloadfilefinish_res")) {
//                                    int status = json.getInt("status");
//                                    int tag = 0;
//
//                                    String filename = "";
//                                    if (json.has("tag")) {
//                                        tag = json.getInt("tag");
//                                    }
//                                    if (json.has("filename")) {
//                                        filename = json.getString("filename");
//                                    } else {
//                                        filename = "";
//                                    }
//
//                                    Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE_FINISH);
//                                    intent.putExtra("status", status);
//                                    intent.putExtra("tag", tag);
//                                    intent.putExtra("filename", filename);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getindexfile_res")) {
//                                    int status = json.getInt("status");
//                                    if (status != MessageToast.GET_INDEX_FILE_SUCCESS) {
//                                        Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE_FILE_FAIL);
//                                        intent.putExtra("status", status);
//                                        sendBroadcast(intent);
//                                    } else {
//                                        if (output != null) {
//                                            output.close();
//                                            output = null;
//                                        }
//                                        File mFilePath = new File(Constance.download_path);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        output = new FileOutputStream(Constance.file_index_path);
//                                        respGetIndexFile(resp);
//                                    }
//                                } else if (cmd_type.equals("deletefile_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_DELETE_FILE);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("streamvideo_res")) {
//                                    String name = json.getString("rtspname");
//                                    int live = json.getInt("live");
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_STREAM_VIDEO);
//                                    intent.putExtra("filename", name);
//                                    intent.putExtra("live", live);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("streamvideofinish_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_STREAM_VIDEO_FINISH);
//                                    intent.putExtra("status", status);
//                                    Log.d("Allen", "streamvideofinish_res123");
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("sendfontfile_res")) {
//                                    int status = json.getInt("status");
//                                    int port = json.getInt("port");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SEND_FONT_FILE);
//                                    intent.putExtra("status", status);
//                                    intent.putExtra("port", port);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getvideostatus_res")) {
//                                    int wdr = json.getInt("wdr");
//                                    int mirror = json.getInt("mirror");
//                                    int flip = json.getInt("flip");
//                                    int fps = json.getInt("fps");
//                                    long bitrate = json.getLong("bitrate");
//                                    int resolution = json.getInt("resolution");
//
//                                    int gop = 0;
//                                    if (json.has("gop")) {
//                                        gop = json.getInt("gop");
//                                    }
//
//
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_VIDEO_STATUS);
//                                    intent.putExtra("wdr", wdr);
//                                    intent.putExtra("mirror", mirror);
//                                    intent.putExtra("flip", flip);
//                                    intent.putExtra("fps", fps);
//                                    intent.putExtra("bitrate", bitrate);
//                                    intent.putExtra("resolution", resolution);
//                                    intent.putExtra("gop", gop);
//                                    sendBroadcast(intent);
//
//                                } else if (cmd_type.equals("getrecordstatus_res")) {
//
//                                    int status = json.getInt("recstatus");
//                                    int volume = json.getInt("volume");
//                                    int length = json.getInt("length");
//                                    int fps = json.getInt("fps");
//                                    long bitrate = json.getLong("bitrate");
//                                    int resolution = json.getInt("resolution");
//                                    int loop = json.getInt("loop");
//                                    int capability = json.getInt("capability");
//                                    int recrunning = 0;
//                                    int gop = json.getInt("gop");
//                                    if (json.has("recrunning")) {
//                                        recrunning = json.getInt("recrunning");
//
//                                    }
//
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_RECORD_STATUS);
//                                    intent.putExtra("recstatus", status);
//                                    intent.putExtra("volume", volume);
//                                    intent.putExtra("length", length);
//                                    intent.putExtra("fps", fps);
//                                    intent.putExtra("bitrate", bitrate);
//                                    intent.putExtra("resolution", resolution);
//                                    intent.putExtra("loop", loop);
//                                    intent.putExtra("capability", capability);
//                                    intent.putExtra("recrunning", recrunning);
//                                    intent.putExtra("gop", gop);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getdeviceparameter_res")) {
//                                    int year = json.getInt("year");
//                                    int month = json.getInt("month");
//                                    int day = json.getInt("day");
//                                    int hour = json.getInt("hour");
//                                    int min = json.getInt("min");
//                                    int sec = json.getInt("sec");
//                                    String timezone = json.getString("timezone");
//                                    int channel = json.getInt("wifichannel");
//                                    String ssid = json.getString("ssid");
//                                    String pwd = json.getString("pwd");
//                                    String iqversion = "";
//                                    int gsensor = 0;
//                                    if (json.has("iqversion"))
//                                        iqversion = Integer.toHexString(json.getInt("iqversion"));
//                                    if (json.has("gsensor_sensitivity"))
//                                        gsensor = json.getInt("gsensor_sensitivity");
//                                    String fwversion = json.getString("fwversion");
//                                    int powerfrequency = json.getInt("powerfrequency");
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_DEVICE_PARAMETER);
//                                    intent.putExtra("year", year);
//                                    intent.putExtra("month", month);
//                                    intent.putExtra("day", day);
//                                    intent.putExtra("hour", hour);
//                                    intent.putExtra("min", min);
//                                    intent.putExtra("sec", sec);
//                                    intent.putExtra("timezone", timezone);
//                                    intent.putExtra("channel", channel);
//                                    intent.putExtra("ssid", ssid);
//                                    intent.putExtra("pwd", pwd);
//                                    intent.putExtra("powerfrequency", powerfrequency);
//                                    if (json.has("iqversion"))
//                                        intent.putExtra("iqversion", iqversion);
//                                    if (json.has("gsensor_sensitivity"))
//                                        intent.putExtra("gsensor_sensitivity", gsensor);
//                                    intent.putExtra("fwversion", fwversion);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("sendfwbin_res")) {
//
//                                    int status = json.getInt("status");//0:success 1:fail
//                                    int port = json.getInt("port");
//                                    Intent intent = new Intent(Constance.BROADCAST_SEND_FW_FILE);
//                                    intent.putExtra("status", status);
//                                    intent.putExtra("port", port);
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("upgradefw_res")) {
//
//                                    int status = json.getInt("status");//0x00:嚙踐�嚙� 0x01:size error	0x02:version error	0x03:md5 error
//
//                                    Intent intent = new Intent(Constance.BROADCAST_UPGRADE_FW);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//
//                                } else if (cmd_type.equals("setlooprecordstatus_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_LOOP_RECORD);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setrecordaudiostatus_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_RECORD_VOLUMN);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setrecordlength_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_RECORD_LENGTH);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setwifiparameters_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_WIFI_PARAMETER);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setrecordparameters_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_RECORD_PARAMETER);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setvideoparameters_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_VIDEO_PARAMETER);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setpowerfrequency_res")) {
//                                    int status = json.getInt("status");//status : 0:success, 1: fail
//
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_POWER_FRQUENCY);
//                                    intent.putExtra("status", status);
//
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getrecordcapability_res")) {
////									int status = json.getInt("status");
//                                    int capability = json.getInt("capability");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_RECORD_CAPABILITY);
////									intent.putExtra("status", status);
//                                    intent.putExtra("capability", capability);
//
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("getosdstatus_res")) {
//                                    int status = json.getInt("status");
//                                    int osd = json.getInt("osd");
//                                    String txt_osd = "";
//                                    if (json.has("unicode")) {
//                                        JSONArray unicode = json.getJSONArray("unicode");
//
//                                        for (int i = 0; i < unicode.length(); i++) {
//
//                                            txt_osd = txt_osd + Character.toString((char) unicode.getInt(i));
//
//                                        }
//                                    }
//                                    Intent intent = new Intent(Constance.BROADCAST_GET_OSD_STATUS);
//                                    intent.putExtra("status", status);
//                                    intent.putExtra("osd", osd);
//                                    intent.putExtra("txt_osd", txt_osd);
//                                    sendBroadcast(intent);
//
//                                } else if (cmd_type.equals("setosdonoff_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_OSD_ON_OFF);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("setgsensorparameter_res")) {
//                                    int status = json.getInt("status");
//                                    Intent intent = new Intent(Constance.BROADCAST_SET_GSENSOR);
//                                    intent.putExtra("status", status);
//
//                                    sendBroadcast(intent);
//                                } else if (cmd_type.equals("nvramresettodefault_res")) {
//                                    int status = json.getInt("status");
//
//                                    Intent intent = new Intent(Constance.BROADCAST_RESET_TO_DEFAULT);
//                                    intent.putExtra("status", status);
//                                    sendBroadcast(intent);
//                                }
//
//                            } catch (JSONException e1) {
//
//                                e1.printStackTrace();
//                                String tmp = new String(resp);
//                                if (tmp.contains("error")) {
//                                    if (tmp.contains("reading")) {
//                                        Toast.makeText(MessageService.this, getString(R.string.sdcard_is_reading), Toast.LENGTH_SHORT).show();
//                                    }
//                                    return;
//                                }
//
//
//                                if (isGetList) {
//                                    listlock.lock();
//                                    if (isGetListFirst) {
//                                        if (output != null) {
//                                            output.close();
//                                            output = null;
//                                        }
//                                        File mFilePath = new File(Constance.download_path);
//                                        if (!mFilePath.exists()) {
//                                            mFilePath.mkdirs();
//                                        }
//                                        output = new FileOutputStream(Constance.file_list_path);
//
//                                        isGetListFirst = false;
//                                        gl_file_size = 0;
//                                        gl_recv_size = 0;
//
//                                        for (int i = 0; i < 4; i++) {
//                                            int value = resp[i];
//                                            if (value < 0)
//                                                value += 256;
//                                            gl_file_size += (value * Math.pow(256, (3 - i)));
//                                        }
//
//                                        gl_recv_size += (resp.length - 4);
//
//                                        byte[] data = new byte[resp.length - 4];
//                                        System.arraycopy(resp, 4, data, 0, resp.length - 4);
//                                        writeToFile(data);
//                                        if (gl_recv_size == gl_file_size) {
//                                            isGetList = false;
//                                            Intent intent = new Intent(Constance.BROADCAST_GET_VIDEO_LIST);
//                                            intent.putExtra("status", MessageToast.GET_VIDEO_LIST_SUCCESS);
//
//                                            sendBroadcast(intent);
//                                        }
//                                        try {
//                                            sleep(50);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        gl_recv_size += resp.length;
//
//                                        writeToFile(resp);
//                                        if (gl_recv_size == gl_file_size) {
//                                            isGetList = false;
//                                            Intent intent = new Intent(Constance.BROADCAST_GET_VIDEO_LIST);
//                                            intent.putExtra("status", MessageToast.GET_VIDEO_LIST_SUCCESS);
//                                            sendBroadcast(intent);
//                                        }
//                                    }
//                                    listlock.unlock();
//                                }
//                            } catch (Exception e) {
//                                Log.d(TAG, "socket receive Exception =" + e.toString());
//
//                            }
//                        }
//                        isConnected = false;
//                        isRunning = false;
//
//                        if (socket != null) {
//                            socket.close();
//                            socket = null;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        isConnected = false;
//                        isRunning = false;
//                        if (socket != null) {
//                            try {
//                                socket.close();
//                            } catch (IOException e1) {
//                                // TODO Auto-generated catch block
//                                e1.printStackTrace();
//                            }
//                            socket = null;
//                        }
//                    }
//                }
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        recv.start();
//    }
//
//    public void close() {
//        isRunning = false;
//
//        if (socket != null) {
//            try {
//
//                socket.close();
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//        }
//        socket = null;
//
//    }
//
//    public class ServiceBinder extends Binder {
//        public Service getService() {
//            return MessageService.this;
//        }
//    }
//
//    public void setDownloadFile(String file, boolean isContinueDownload) {
//        filename = file;
//        gl_isDownloadcontinue = isContinueDownload;
//        if (isContinueDownload == false) {
//            gl_record_recv_size = 0;
//        }
//    }
//
//    public void setDownloadFileType(int type) {
//        filetype = type;
//    }
//
//    int recv_size = 0;
//
//    //	boolean gl_isThumbnail=false;
//    private void respDownloadFile(final byte[] resp) {
//        Log.d("Allen", "respDownloadFile start");
//        Thread t = new Thread() {
//            public void run() {
//                int tag = 0;
//                Socket mSocket = null;
//                try {
//                    JSONObject obj = new JSONObject(new String(resp));
//                    int status = obj.getInt("status");
//                    int port = obj.getInt("port");
//
//                    if (obj.has("tag")) {
//                        tag = obj.getInt("tag");
//                        Log.d("Allen", "Donwloadfile tag =" + obj.getInt("tag"));
//                    }
//
//                    Log.d("Allen", "respDownloadFile 111");
//                    if (status == MessageToast.DOWNLOAD_FILE_SUCCESS) {
//                        mSocket = new Socket("192.168.99.1", port);
//                        mSocket.setTcpNoDelay(true);
//                        mSocket.setReceiveBufferSize(65024);//64000
//                        mSocket.setSoTimeout(Constance.DEFAULT_DOWNLOAD_TIMEOUT);
//
//                        InputStream mInput = mSocket.getInputStream();
//
//                        byte[] recvBuf = new byte[64000];
//                        int num = 0;
//
//                        recv_size = 0;
//                        int file_size = 0;
//                        boolean first = true;
////						openDownloadNotification(filename);
//                        int tmpPercent = 0;
//                        int nowPercent = 0;
//                        while ((num = mInput.read(recvBuf)) != -1) {
//
//                            int offset = 0;
//
//                            if (first) {
//
//                                first = false;
//                                for (int i = 0; i < num; i++) {
//                                    int value = recvBuf[i];
//                                    if (value < 0)
//                                        value += 256;
//                                    file_size += (value * Math.pow(256, (3 - i)));
//                                }
//
//                                if (file_size == 0)
//                                    break;
//                                if (gl_isDownloadcontinue) {
////									file_size=file_size+gl_record_recv_size;
//                                    recv_size = recv_size + gl_record_recv_size;
//                                }
//                                if (num == 4) {
//                                    continue;
//                                } else {
//                                    offset = 4;
//                                }
//                            }
//
//
//                            byte[] data = new byte[num - offset];
//
//                            System.arraycopy(recvBuf, offset, data, 0, (num - offset));
//
//
//                            recv_size += (num - offset);
//                            if (tag != 0) {
//                                if (recv_size >= file_size) {
//                                    int index = 0;
//                                    for (int i = (data.length - 1); i > 0; i--) {
//                                        if (data[i] == (byte) 0xd9) {
//                                            if ((data[i - 1]) == (byte) 0xff) {
//                                                index = i;
//                                                Log.d("Allen", "cut zero index= " + index);
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                    writeToFile(data, data.length - (data.length - 1 - index));
//                                } else {
//                                    writeToFile(data);
//                                }
//                            } else {
//                                writeToFile(data);
//                            }
//
//
//                            nowPercent = percent(recv_size, file_size);
//
//                            if (tmpPercent == nowPercent) {
//
//                            } else {
//                                tmpPercent = nowPercent;
//                                Intent i = new Intent(Constance.BROADCAST_DOWNLOAD_PERCENT);
//                                i.putExtra("percent", tmpPercent);
//                                sendBroadcast(i);
////								setDownloadProgress(tmpPercent);
//                            }
//
//                            if (recv_size >= file_size) {
//                                break;
//                            }
//                        }
//
//                        if (recv_size >= file_size) {
//
////							downloadFinish();
//                            gl_isDownloadcontinue = false;
//                            Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                            intent.putExtra("filename", filename);
//                            intent.putExtra("tag", tag);
//
//                            intent.putExtra("status", MessageToast.DOWNLOAD_FILE_SUCCESS);
//                            gl_record_recv_size = 0;
//                            Log.d("Allen", "message send boadcast....");
//                            sendBroadcast(intent);
//                        } else {
////							deleteRecordFile();
//
//                            Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                            intent.putExtra("filename", filename);
//                            intent.putExtra("status", MessageToast.DOWNLOAD_FILE_FAIL);
//                            intent.putExtra("tag", tag);
//                            intent.putExtra("pos", recv_size);
//
//                            gl_record_recv_size = recv_size;
//                            sendBroadcast(intent);
//                        }
//                        mInput.close();
//                        mSocket.close();
//                        mSocket = null;
//                    } else {
//
//                    }
//                } catch (JSONException e) {
////					deleteRecordFile();
//
//
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                    intent.putExtra("filename", filename);
//                    intent.putExtra("status", MessageToast.DOWNLOAD_FILE_FAIL);
//                    intent.putExtra("pos", recv_size);
//                    intent.putExtra("tag", tag);
//                    gl_record_recv_size = recv_size;
//                    sendBroadcast(intent);
//                    try {
//                        mSocket.close();
//                    } catch (IOException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    } catch (Exception exception) {
//                        Log.e("respdownload Socket Exception", exception.toString());
//                    }
//                    mSocket = null;
//                } catch (IOException e) {
////					deleteRecordFile();
//
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                    intent.putExtra("filename", filename);
//
//                    intent.putExtra("status", MessageToast.DOWNLOAD_FILE_FAIL);
//                    intent.putExtra("pos", recv_size);
//                    intent.putExtra("tag", tag);
//                    gl_record_recv_size = recv_size;
//                    sendBroadcast(intent);
//                    try {
//                        if (mSocket != null)
//                            mSocket.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                    mSocket = null;
//                } catch (Exception e) {
//
////					deleteRecordFile();
//
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_DOWNLOAD_FILE);
//                    intent.putExtra("filename", filename);
//                    intent.putExtra("tag", tag);
//                    intent.putExtra("status", MessageToast.DOWNLOAD_FILE_FAIL);
//                    intent.putExtra("pos", recv_size);
//                    gl_record_recv_size = recv_size;
//                    sendBroadcast(intent);
//                    try {
//                        mSocket.close();
//                    } catch (IOException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    } catch (Exception exception) {
//                        exception.printStackTrace();
//                    }
//                    mSocket = null;
//                }
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t.start();
//    }
//
//    public void deleteRecordFile() {
//
////		downloadFail();
//        String filePath = Constance.download_path + "/record/" + filename;
//        File file = new File(filePath);
//        if (!file.exists()) {
//            return;
//        }
//        file.delete();
//    }
//
//    private void respGetIndexFile(final byte[] resp) {
//
//        Thread t = new Thread() {
//            public void run() {
//                try {
//
//                    JSONObject obj = new JSONObject(new String(resp));
//                    int status = obj.getInt("status");
//                    int port = obj.getInt("port");
//                    if (status == MessageToast.GET_INDEX_FILE_SUCCESS) {
//                        Socket mSocket = new Socket("192.168.99.1", port);
//
//                        InputStream mInput = mSocket.getInputStream();
//                        byte[] recvBuf = new byte[64000];
//                        int num = 0;
//                        int recv_size = 0;
//                        int file_size = 0;
//                        boolean first = true;
//                        while ((num = mInput.read(recvBuf)) != -1) {
//                            if (first) {
//                                first = false;
//                                for (int i = 0; i < num; i++) {
//                                    int value = recvBuf[i];
//                                    if (value < 0)
//                                        value += 256;
//                                    file_size += (value * Math.pow(256, (3 - i)));
//
//                                }
//
//                                if (file_size == 0) {
//
//                                    break;
//                                }
//
//                                if (num == 4) {
//                                    continue;
//                                }
//                            }
//
//                            byte[] data = new byte[num];
//                            System.arraycopy(recvBuf, 0, data, 0, num);
//                            recv_size += num;
//
//                            writeToFile(data);
//
//                            if (recv_size == file_size) {
//
//                                break;
//                            }
//                        }
//
//                        if (recv_size == file_size) {
//                            Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                            intent.putExtra("status", MessageToast.GET_INDEX_FILE_SUCCESS);
//                            if (obj.has("sdisfull"))
//                                intent.putExtra("sdisfull", obj.getInt("sdisfull"));
//                            sendBroadcast(intent);
//                        } else {
//                            Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                            intent.putExtra("status", MessageToast.GET_INDEX_FILE_FAIL);
//                            if (obj.has("sdisfull"))
//                                intent.putExtra("sdisfull", obj.getInt("sdisfull"));
//                            sendBroadcast(intent);
//                        }
//                    } else if (status == MessageToast.GET_INDEX_FILE_FAIL) {
//                        Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                        intent.putExtra("status", MessageToast.GET_INDEX_FILE_FAIL);
//
//                        sendBroadcast(intent);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                    intent.putExtra("status", MessageToast.GET_INDEX_FILE_FAIL);
//                    sendBroadcast(intent);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                    intent.putExtra("status", MessageToast.GET_INDEX_FILE_FAIL);
//                    sendBroadcast(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Intent intent = new Intent(Constance.BROADCAST_GET_INDEX_FILE);
//                    intent.putExtra("status", MessageToast.GET_INDEX_FILE_FAIL);
//                    sendBroadcast(intent);
//                }
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t.start();
//    }
//    FileOutputStream output = null;
//
//    private void writeToFile(byte[] data) {
//        try {
//            if (output == null) {
//                File mFilePath = new File(Constance.download_path);
//                if (!mFilePath.exists()) {
//                    mFilePath.mkdirs();
//                }
//                output = new FileOutputStream(Constance.download_path + "/" + "unknow" + ".avi");
//            }
//            output.write(data);
//
//        } catch (FileNotFoundException e) {
//
//        } catch (IOException e) {
//
//        }
//    }
//
//    private void writeToFile(byte[] data, int length) {
//        try {
//            if (output == null) {
//                File mFilePath = new File(Constance.download_path);
//                if (!mFilePath.exists()) {
//                    mFilePath.mkdirs();
//                }
//                output = new FileOutputStream(Constance.download_path + "/" + "unknow" + ".avi");
//            }
//            output.write(data, 0, length);
//
//        } catch (FileNotFoundException e) {
//
//        } catch (IOException e) {
//
//        }
//    }
//
//    private String getFileSaveName(String name, String type) {
//        String strFileName = name;
//        File file = new File(name);
//        if (file.exists()) {
//            for (int i = 1; ; i++) {
//                strFileName = name.replace(type, " (" + i + ")" + type);
//                File tmpFile = new File(strFileName);
//                if (!tmpFile.exists()) {
//                    break;
//                }
//            }
//        }
//        return strFileName;
//    }
//}