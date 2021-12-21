package dev.joxit.androidapp.audiorecorder.activity.service.recorder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_CALL_STATE
import android.telephony.TelephonyManager


class CallReceiver : BroadcastReceiver() {
  var isOngoingCall = false
    private set
  var isRinging = false
    private set
  private var mListener: CallReceiverListener? = null
  private var mTelephony: TelephonyManager? = null

  interface CallReceiverListener {
    fun onIdleState()
    fun onOffHookState()
  }

  fun setListener(callReceiverListener: CallReceiverListener?) {
    mListener = callReceiverListener
  }

  override fun onReceive(context: Context, intent: Intent) {
    mTelephony = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
    mTelephony!!.listen(CallStateListener(), LISTEN_CALL_STATE)
  }

  inner class CallStateListener : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, phoneNumber: String) {
      mTelephony!!.listen(this, LISTEN_NONE)
      when (state) {
        TelephonyManager.CALL_STATE_IDLE -> {
          isOngoingCall = false
          isRinging = false
          mListener?.onIdleState()
        }
        TelephonyManager.CALL_STATE_RINGING -> {
          isOngoingCall = false
          isRinging = false
        }
        TelephonyManager.CALL_STATE_OFFHOOK -> {
          isOngoingCall = true
          isRinging = false
          mListener?.onOffHookState()

        }
      }
    }
  }
}
