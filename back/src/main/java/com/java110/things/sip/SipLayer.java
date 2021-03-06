package com.java110.things.sip;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.DeviceConstants;
import com.java110.things.entity.sip.Device;
import com.java110.things.entity.sip.DeviceChannel;
import com.java110.things.entity.sip.Host;
import com.java110.things.entity.sip.PushStreamDevice;
import com.java110.things.sip.message.helper.DigestServerAuthenticationHelper;
import com.java110.things.sip.message.helper.SipContentHelper;
import com.java110.things.sip.message.session.MessageManager;
import com.java110.things.sip.session.PushStreamDeviceManager;
import com.java110.things.util.IDUtils;
import com.java110.things.util.PortUtils;
import com.java110.things.util.RedisUtil;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Expires;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.*;

public class SipLayer implements SipListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private SipStackImpl mSipStack;

    private AddressFactory mAddressFactory;

    private HeaderFactory mHeaderFactory;

    private MessageFactory mMessageFactory;

    private SipProvider mTCPSipProvider;
    private SipProvider mUDPSipProvider;

    private long mCseq = 1;
    private long mSN = 1;

    private String mLocalIp;
    private int mLocalPort;
    private String mSipId;
    private String mSipRealm;
    private String mPassword;
    private String mSsrcRealm;
    private String mStreamMediaIp;

    private DigestServerAuthenticationHelper mDigestServerAuthenticationHelper;
    public static final String UDP = "UDP";
    public static final String TCP = "TCP";

    private static final String MESSAGE_CATALOG = "Catalog";
    private static final String MESSAGE_DEVICE_INFO = "DeviceInfo";
    private static final String MESSAGE_BROADCAST = "Broadcast";
    private static final String MESSAGE_DEVICE_STATUS = "DeviceStatus";
    private static final String MESSAGE_KEEP_ALIVE = "Keepalive";
    private static final String MESSAGE_MOBILE_POSITION = "MobilePosition";
    private static final String MESSAGE_MOBILE_POSITION_INTERVAL = "Interval";

    private static final String ELEMENT_DEVICE_ID = "DeviceID";
    private static final String ELEMENT_DEVICE_LIST = "DeviceList";
    private static final String ELEMENT_NAME = "Name";
    private static final String ELEMENT_STATUS = "Status";

    private static final int STREAM_MEDIA_START_PORT = 20000;
    private static final int STREAM_MEDIA_END_PORT = 21000;
    private int mStreamPort = STREAM_MEDIA_START_PORT;


    private static final int SSRC_TYPE_PLAY = 0;
    private static final int SSRC_TYPE_HISTORY = 1;
    private static final int SSRC_MIN_VALUE = 0;
    private static final int SSRC_MAX_VALUE = 9999;
    private int mSsrc;

    public static final String SESSION_NAME_PLAY = "Play";
    public static final String SESSION_NAME_DOWNLOAD = "Download";
    public static final String SESSION_NAME_PLAY_BACK = "Playback";


    private MessageManager mMessageManager = MessageManager.getInstance();
    private PushStreamDeviceManager mPushStreamDeviceManager = PushStreamDeviceManager.getInstance();

    public SipLayer(String sipId, String sipRealm, String password, String localIp, int localPort, String streamMediaIp,int streamMediaPort) {
        this.mSipId = sipId;
        this.mLocalIp = localIp;
        this.mLocalPort = localPort;
        this.mSipRealm = sipRealm;
        this.mPassword = password;
        this.mSsrcRealm = mSipId.substring(3, 8);
        this.mStreamMediaIp = streamMediaIp;
    }

    public boolean startServer() {
        return initSip();
    }

    @SuppressWarnings("deprecation")
    private boolean initSip() {
        try {
            SipFactory sipFactory = SipFactory.getInstance();
            Properties properties = new Properties();
            properties.setProperty("javax.sip.STACK_NAME", "GB28181_SIP");
            properties.setProperty("javax.sip.IP_ADDRESS", mLocalIp);
            mSipStack = (SipStackImpl) sipFactory.createSipStack(properties);

            mHeaderFactory = sipFactory.createHeaderFactory();
            mAddressFactory = sipFactory.createAddressFactory();
            mMessageFactory = sipFactory.createMessageFactory();
            mDigestServerAuthenticationHelper = new DigestServerAuthenticationHelper();
            //????????????UDP???TCP
            try {
                ListeningPoint tcpListeningPoint = mSipStack.createListeningPoint(mLocalIp, mLocalPort, "TCP");

                ListeningPoint udpListeningPoint = mSipStack.createListeningPoint(mLocalIp, mLocalPort, "UDP");

                mTCPSipProvider = mSipStack.createSipProvider(tcpListeningPoint);
                mTCPSipProvider.addSipListener(this);

                mUDPSipProvider = mSipStack.createSipProvider(udpListeningPoint);
                mUDPSipProvider.addSipListener(this);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * ???????????????Request??????
     */
    public void processRequest(RequestEvent evt) {
        Request request = evt.getRequest();
        String method = request.getMethod();
        logger.info("processRequest >>> {}", request);
        try {
            if (method.equalsIgnoreCase(Request.REGISTER)) {
                processRegister(evt);
            } else if (method.equalsIgnoreCase(Request.MESSAGE)) {
                processMessage(evt);
            } else if (method.equalsIgnoreCase(Request.BYE)) {
                processBye(evt);
            }
        } catch (Exception e) {
            logger.error("??????????????? ????????????", e);
        }
    }


    private void processBye(RequestEvent evt) throws Exception {
        ServerTransaction serverTransaction = evt.getServerTransaction();
        Dialog dialog = serverTransaction != null ? serverTransaction.getDialog() : null;

        Request request = evt.getRequest();
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        close(mPushStreamDeviceManager.removeByCallId(callIdHeader.getCallId()));

        if (serverTransaction == null || dialog == null) {
            serverTransaction = (isTCP(request) ? mTCPSipProvider : mUDPSipProvider).getNewServerTransaction(request);
        }
        Response response = mMessageFactory.createResponse(Response.OK, request);
        serverTransaction.sendResponse(response);


    }

    private void processMessage(RequestEvent evt) throws Exception {
        Request request = evt.getRequest();
        SAXReader reader = new SAXReader();
        //reader.setEncoding("GB2312");
        Document xml = reader.read(new ByteArrayInputStream(request.getRawContent()));
        Element rootElement = xml.getRootElement();
        String cmd = rootElement.element("CmdType").getStringValue();
        Element deviceIdElement = rootElement.element(ELEMENT_DEVICE_ID);
        if (deviceIdElement == null) {
            return;
        }
        String deviceId = deviceIdElement.getText().toString();
        Response response = null;

        //?????????
        //??????redis???????????????????????????
        //???????????????400
        if (MESSAGE_KEEP_ALIVE.equals(cmd)) {
            if (RedisUtil.checkExist(deviceId)) {
                RedisUtil.expire(deviceId, RedisUtil.EXPIRE);
            } else {
                response = mMessageFactory.createResponse(Response.BAD_REQUEST, request);
            }
        }

        //????????????????????????redis
        else if (MESSAGE_CATALOG.equals(cmd)) {

            Element deviceListElement = rootElement.element(ELEMENT_DEVICE_LIST);
            if (deviceListElement == null) {
                return;
            }
            Iterator<Element> deviceListIterator = deviceListElement.elementIterator();
            if (deviceListIterator != null) {
                String deviceStr = RedisUtil.get(deviceId);
                if (StringUtils.isEmpty(deviceStr)) {
                    return;
                }
                Device device = JSONObject.parseObject(deviceStr, Device.class);
                Map<String, DeviceChannel> channelMap = device.getChannelMap();
                if (channelMap == null) {
                    channelMap = new HashMap<String, DeviceChannel>(5);
                    device.setChannelMap(channelMap);
                }
                //??????DeviceList
                while (deviceListIterator.hasNext()) {
                    Element itemDevice = deviceListIterator.next();

                    Element channelDeviceElement = itemDevice.element(ELEMENT_DEVICE_ID);

                    if (channelDeviceElement == null) {
                        continue;
                    }
                    String channelDeviceId = channelDeviceElement.getText().toString();
                    Element channdelNameElement = itemDevice.element(ELEMENT_NAME);
                    String channelName = channdelNameElement != null ? channdelNameElement.getText().toString() : "";
                    Element statusElement = itemDevice.element(ELEMENT_STATUS);
                    String status = statusElement != null ? statusElement.getText().toString() : "ON";

                    DeviceChannel deviceChannel = channelMap.containsKey(channelDeviceId) ? channelMap.get(channelDeviceId) : new DeviceChannel();
                    deviceChannel.setName(channelName);
                    deviceChannel.setDeviceId(channelDeviceId);
                    deviceChannel.setStatus(status.equals("ON") ? DeviceConstants.ON_LINE : DeviceConstants.OFF_LINE);

                    channelMap.put(channelDeviceId, deviceChannel);
                }
                //??????Redis
                RedisUtil.set(deviceId, JSONObject.toJSONString(device));
            }
        }
        if (response == null) {
            response = mMessageFactory.createResponse(Response.OK, request);
        }
        sendResponse(response, getServerTransaction(evt));

    }

    private ServerTransaction getServerTransaction(RequestEvent evt) throws Exception {
        ServerTransaction serverTransaction = evt.getServerTransaction();
        if (serverTransaction == null) {
            Request request = evt.getRequest();
            serverTransaction = (isTCP(request) ? mTCPSipProvider : mUDPSipProvider).getNewServerTransaction(request);
        }
        return serverTransaction;

    }

    private void processRegister(RequestEvent evt) throws Exception {
        Request request = evt.getRequest();
        ServerTransaction serverTransaction = evt.getServerTransaction();
        boolean isTcp = isTCP(request);
        if (serverTransaction == null) {
            serverTransaction = (isTCP(request) ? mTCPSipProvider : mUDPSipProvider).getNewServerTransaction(request);
        }
        Response response = null;
        boolean passwordCorrect = false;
        boolean isRegisterSuceess = false;
        Device device = null;
        Header header = request.getHeader(AuthorizationHeader.NAME);
        //???????????????
        //????????????????????????
        if (header != null) {
            passwordCorrect = mDigestServerAuthenticationHelper.doAuthenticatePlainTextPassword(request, mPassword);
            if (!passwordCorrect) {
                logger.info("????????????");
            }
        }

        //???????????????????????????????????? ??????401
        if (header == null || !passwordCorrect) {
            response = mMessageFactory.createResponse(Response.UNAUTHORIZED, request);
            mDigestServerAuthenticationHelper.generateChallenge(mHeaderFactory, response, mSipRealm);
        }
        //?????????????????????????????????
        else if (header != null && passwordCorrect) {
            response = mMessageFactory.createResponse(Response.OK, request);
            //??????date???
            response.addHeader(mHeaderFactory.createDateHeader(Calendar.getInstance(Locale.ENGLISH)));
            ExpiresHeader expiresHeader = (ExpiresHeader) request.getHeader(Expires.NAME);
            //??????Contact???
            response.addHeader(request.getHeader(ContactHeader.NAME));
            //??????Expires???
            response.addHeader(request.getExpires());
            //????????????
            if (expiresHeader != null && expiresHeader.getExpires() == 0) {

            }
            //????????????
            else {
                isRegisterSuceess = true;
                //1.??????????????????????????????????????????Redis
                FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
                ViaHeader viaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                Host host = getHost(viaHeader);
                String deviceId = getDeviceId(fromHeader);
                device = new Device();
                device.setDeviceId(deviceId);
                device.setHost(host);
                device.setProtocol(isTcp ? TCP : UDP);

            }
        }
        sendResponse(response, serverTransaction);
        //????????????
        //1.?????????redis
        //2.??????catelog????????????
        if (isRegisterSuceess && device != null) {
            String callId = IDUtils.id();
            String fromTag = IDUtils.id();
            RedisUtil.set(device.getDeviceId(), RedisUtil.EXPIRE, JSONObject.toJSONString(device));
            sendCatalog(device, callId, fromTag, mCseq, String.valueOf(mSN));
        }
    }

    private void sendCatalog(Device device, String callId, String fromTag, long cseq, String sn) throws Exception {
        Host host = device.getHost();
        String deviceId = device.getDeviceId();
        Request request = createRequest(deviceId, host.getAddress(), host.getWanIp(), host.getWanPort(), device.getProtocol(),
                mSipId, mSipRealm, fromTag,
                deviceId, mSipRealm, null,
                callId, cseq, Request.MESSAGE);
        String catalogContent = SipContentHelper.generateCatalogContent(deviceId, sn);
        ContentTypeHeader contentTypeHeader = mHeaderFactory.createContentTypeHeader("Application", "MANSCDP+xml");
        request.setContent(catalogContent, contentTypeHeader);
        request.addHeader(contentTypeHeader);

        sendRequest(request);

    }

    public void sendInvite(Device device, String sessionName, String callId, String channelId, int port, String ssrc,
                           boolean isTcp) throws Exception {

        String fromTag = IDUtils.id();
        Host host = device.getHost();
        String realm = channelId.substring(0, 8);
        Request request = createRequest(channelId, host.getAddress(), host.getWanIp(), host.getWanPort(), device.getProtocol(),
                mSipId, mSipRealm, fromTag,
                channelId, realm, null,
                callId, 20, Request.INVITE);
        //??????Concat???
        Address concatAddress = mAddressFactory.createAddress(mAddressFactory.createSipURI(mSipId, mLocalIp.concat(":").concat(String.valueOf(mLocalPort))));
        request.addHeader(mHeaderFactory.createContactHeader(concatAddress));
        //???????????????
        String content = SipContentHelper.generateRealTimeMeidaStreamInviteContent(mSipId, mStreamMediaIp, port, isTcp, false, sessionName, ssrc);
        ContentTypeHeader contentTypeHeader = mHeaderFactory.createContentTypeHeader("Application", "SDP");
        request.setContent(content, contentTypeHeader);
        //request.addHeader(contentTypeHeader);
        sendRequest(request);
    }

    private Request createRequest(String deviceId, String address, String targetIp, int targetPort, String protocol,
                                  String fromUserInfo, String fromHostPort, String fromTag,
                                  String toUserInfo, String toHostPort, String toTag,
                                  String callId,
                                  long cseqNo, String method) throws ParseException, InvalidArgumentException {
        //?????????
        SipURI requestLine = mAddressFactory.createSipURI(deviceId, address);
        //Via???
        ArrayList viaHeaderList = new ArrayList();
        ViaHeader viaHeader = mHeaderFactory.createViaHeader(targetIp, targetPort, protocol, null);
        viaHeader.setRPort();
        viaHeaderList.add(viaHeader);

        //To???
        SipURI toAddress = mAddressFactory.createSipURI(toUserInfo, toHostPort);
        Address toNameAddress = mAddressFactory.createAddress(toAddress);
        ToHeader toHeader = mHeaderFactory.createToHeader(toNameAddress, toTag);

        //From???
        SipURI from = mAddressFactory.createSipURI(fromUserInfo, fromHostPort);
        Address fromNameAddress = mAddressFactory.createAddress(from);
        FromHeader fromHeader = mHeaderFactory.createFromHeader(fromNameAddress, fromTag);

        //callId
        CallIdHeader callIdHeader = protocol.equals(TCP) ? mTCPSipProvider.getNewCallId() : mUDPSipProvider.getNewCallId();
        ;
        callIdHeader.setCallId(callId);

        //Cseq
        CSeqHeader cSeqHeader = mHeaderFactory.createCSeqHeader(cseqNo, method);

        //Max_forward
        MaxForwardsHeader maxForwardsHeader = mHeaderFactory.createMaxForwardsHeader(70);

        return mMessageFactory.createRequest(requestLine, method, callIdHeader, cSeqHeader, fromHeader, toHeader,
                viaHeaderList, maxForwardsHeader);

    }

    private boolean isTCP(Request request) {
        return isTCP((ViaHeader) request.getHeader(ViaHeader.NAME));
    }

    private boolean isTCP(Response response) {
        return isTCP((ViaHeader) response.getHeader(ViaHeader.NAME));
    }

    private boolean isTCP(ViaHeader viaHeader) {
        String protocol = viaHeader.getProtocol();
        return protocol.equals("TCP");
    }

    private String getDeviceId(FromHeader fromHeader) {
        AddressImpl address = (AddressImpl) fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        String user = uri.getUser();
        return user;
    }

    private Host getHost(ViaHeader viaHeader) {
        String received = viaHeader.getReceived();
        int rPort = viaHeader.getRPort();
        //?????????????????? received ?????? rPort ??? -1
        //????????????????????????
        if (StringUtils.isEmpty(received) || rPort == -1) {
            received = viaHeader.getHost();
            rPort = viaHeader.getPort();
        }
        Host host = new Host();
        host.setWanIp(received);
        host.setWanPort(rPort);
        host.setAddress(received.concat(":").concat(String.valueOf(rPort)));
        return host;
    }

    private void sendResponse(Response response, ServerTransaction serverTransaction) throws Exception {
        logger.info("sendResponse >>> {}", response);
        serverTransaction.sendResponse(response);
    }

    private void sendRequest(Request request) throws SipException {
        logger.info("sendRequest >>> {}", request);
        ClientTransaction clientTransaction = (isTCP(request) ? mTCPSipProvider : mUDPSipProvider).getNewClientTransaction(request);
        clientTransaction.sendRequest();
    }

    /**
     * ???????????????Response??????
     * ???????????????????????????Request????????????
     */
    public void processResponse(ResponseEvent evt) {
        Response response = evt.getResponse();
        logger.info("processResponse >>> {}", response);
        CSeqHeader cseqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        String method = cseqHeader.getMethod();
        try {
            //?????????????????????
            if (Request.INVITE.equals(method)) {
                int statusCode = response.getStatusCode();
                //trying????????????
                if (statusCode == Response.TRYING) {

                }
                //????????????
                //??????ack
                if (statusCode == Response.OK) {
                    ClientTransaction clientTransaction = evt.getClientTransaction();
                    if (clientTransaction == null) {
                        logger.error("??????ACK??????clientTransaction???null >>> {}", response);
                        return;
                    }
                    Dialog clientDialog = clientTransaction.getDialog();

                    CSeqHeader clientCSeqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
                    long cseqId = clientCSeqHeader.getSeqNumber();
					/*
					createAck??????????????????ackRequest????????????Invite?????????200OK?????????contact??????????????????????????????????????????
					????????????????????????????????????????????????????????????ack??????????????????????????????????????????
					??????????????????????????????????????????????????????Via??????????????????????????????
					 */
                    Request ackRequest = clientDialog.createAck(cseqId);
                    SipURI requestURI = (SipURI) ackRequest.getRequestURI();
                    ViaHeader viaHeader = (ViaHeader) response.getHeader(ViaHeader.NAME);
                    requestURI.setHost(viaHeader.getHost());
                    requestURI.setPort(viaHeader.getPort());
                    clientDialog.sendAck(ackRequest);
                    CallIdHeader callIdHeader = (CallIdHeader) ackRequest.getHeader(CallIdHeader.NAME);
                    //?????????????????????
                    mMessageManager.put(callIdHeader.getCallId(), clientDialog);
                    logger.info("sendAck >>> {}", ackRequest);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    /**
     * ?????????????????????session???????????????
     *
     * @throws SipException
     */
    private void close(PushStreamDevice pushStreamDevice) throws SipException {
        if (pushStreamDevice != null) {
            pushStreamDevice.getServer().stopServer();
            pushStreamDevice.getObserver().stopRemux();
        }
    }

    public void sendBye(String callId) throws SipException {
        PushStreamDevice pushStreamDevice = mPushStreamDeviceManager.removeByCallId(callId);
        if (pushStreamDevice != null) {
            close(pushStreamDevice);
            Dialog dialog = pushStreamDevice.getDialog();
            if (dialog != null) {
                Request byeRequest = dialog.createRequest(Request.BYE);
                ClientTransaction clientTransaction = (isTCP(byeRequest) ? mTCPSipProvider : mUDPSipProvider).getNewClientTransaction(byeRequest);
                dialog.sendRequest(clientTransaction);
                logger.info("sendRequest >>> {}", byeRequest);
            }
        }

    }

    public String getSsrc(boolean isRealTime) {
        StringBuffer buffer = new StringBuffer(15);
        buffer.append(String.valueOf(isRealTime ? 0 : 1));
        buffer.append(mSsrcRealm);
        if (mSsrc >= SSRC_MAX_VALUE) {
            mSsrc = SSRC_MIN_VALUE;
        }
        String ssrcStr = String.valueOf(mSsrc);
        int length = ssrcStr.length();
        for (int i = length; i < 4; i++) {
            buffer.append("0");
        }
        buffer.append(String.valueOf(ssrcStr));

        return buffer.toString();
    }

    public int getPort() {
        int resultPort = 0;
        resultPort = mStreamPort;
        return resultPort;
    }

    public int getPort(boolean isTcp) {
        int resultPort = 0;
        if (PortUtils.isUsing(isTcp, mStreamPort)) {
            resultPort = PortUtils.findAvailablePortRange(STREAM_MEDIA_START_PORT, STREAM_MEDIA_END_PORT, isTcp);
        }
        resultPort = mStreamPort;
        mStreamPort++;
        if (mStreamPort > STREAM_MEDIA_END_PORT) {
            mStreamPort = STREAM_MEDIA_START_PORT;
        }
        return resultPort;
    }

    /**
     * ????????????
     */
    public void processDialogTerminated(DialogTerminatedEvent evt) {

    }

    /**
     * UDPSocket????????????
     */
    public void processIOException(IOExceptionEvent evt) {

    }

    /**
     * ??????
     * ?????????Request??????????????????
     */
    public void processTimeout(TimeoutEvent evt) {

    }

    /**
     * ????????????
     */
    public void processTransactionTerminated(TransactionTerminatedEvent evt) {

    }

}
