package com.xhzh.shounaxiang.util;

/**
 * Created by wjsay on 2018/10/24
 * Describe:
 */

import android.content.Context;
import android.util.Log;

import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.alibaba.idst.nls.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.internal.protocol.NlsRequestProto;

/**
 * 语音识别类
 * 用于将语音转换为文字
 */
public class AudioSoundRecognizer {
    private static final String TAG = "AudioSoundRecognizer";

    private static final String appKey = "nls-service";
    private static final String id = "LTAILtpaW9KOS53M";
    private static final String secret = "iL8LEpKJAGjCsA93sGgc0TD005yovA";

    private boolean recognizing;
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;

    /**
     * 实例化语音识别
     *
     * @param listener      异步语音服务结果的回调类，回调参数：
     *                      NlsClient.ErrorCode.SUCCESS:
     *                      NlsClient.ErrorCode.RECOGNIZE_ERROR:
     *                      NlsClient.ErrorCode.RECORDING_ERROR:
     *                      NlsClient.ErrorCode.NOTHING:
     * @param stageListener 语音服务引擎状态变更回调接口，服务状态的改变
     *                      音量大小的回调、语音文件的生成通过本接口获取。
     */
    public AudioSoundRecognizer(Context context, NlsListener listener, StageListener stageListener) {
        NlsClient.openLog(true);
        NlsClient.configure(context.getApplicationContext());

        mNlsRequest = new NlsRequest(new NlsRequestProto(context));
        mNlsRequest.setApp_key(appKey);
        mNlsRequest.setAsr_sc("opu");

        //实例化NlsClient
        mNlsClient = NlsClient.newInstance(context, listener, stageListener, mNlsRequest);
        mNlsClient.setMaxRecordTime(60000);         //设置最长语音
        mNlsClient.setMaxStallTime(1000);           //设置最短语音
        mNlsClient.setMinRecordTime(500);           //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);        //设置VAD
        mNlsClient.setMinVoiceValueInterval(100);   //设置音量回调时长
    }

    /**
     * 开始识别
     */
    public void startRecognize() {
        recognizing = true;
        mNlsRequest.authorize(id, secret);
        if (!mNlsClient.start()){
            Log.e(TAG, "startRecognize: can not start recognize");
        }
    }

    /**
     * 停止识别
     */
    public void stopRecognize() {
        if (recognizing)
            mNlsClient.stop();
        recognizing = false;
    }
}

