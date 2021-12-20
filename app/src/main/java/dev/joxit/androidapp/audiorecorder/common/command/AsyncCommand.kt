package dev.joxit.androidapp.audiorecorder.common.command

import android.content.Context
import android.os.AsyncTask
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


abstract class AsyncCommand(context: Context, commandListener: CommandListener?) {
  val context: Context = context.applicationContext
  private var mAsyncTask: MyAsyncTask? = null
  private val mListener: CommandListener? = commandListener

  companion object {
    private const val CORE_POOL_SIZE = 5
    private const val KEEP_ALIVE = 1L
    private const val MAXIMUM_POOL_SIZE = 128
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(10)
    private val sThreadFactory: ThreadFactory = object : ThreadFactory {
      private val mCount = AtomicInteger(1)
      override fun newThread(runnable: Runnable): Thread {
        return Thread(runnable, "AsyncCommand #" + mCount.getAndIncrement())
      }
    }
    val THREAD_POOL_EXECUTOR: Executor =
      ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE,
        TimeUnit.SECONDS,
        sPoolWorkQueue,
        sThreadFactory
      )
  }

  interface CommandListener {
    fun onCommandFinished(z: Boolean)
  }

  open fun execute(): Boolean {
    return true
  }

  open fun onPostExecute(z: Boolean) {}

  fun onPreExecute(): Boolean {
    return true
  }

  fun start() {
    mAsyncTask = MyAsyncTask()
    mAsyncTask!!.executeOnExecutor(THREAD_POOL_EXECUTOR)
  }

  fun startSync() {
    val execute = onPreExecute() && execute()
    onPostExecute(execute)
    mListener?.onCommandFinished(execute)
  }

  val isRunning: Boolean
    get() {
      val myAsyncTask = mAsyncTask ?: return false
      val status = myAsyncTask.status
      return when {
        mAsyncTask!!.isCancelled -> {
          mAsyncTask = null
          false
        }
        status != AsyncTask.Status.FINISHED -> {
          true
        }
        else -> {
          mAsyncTask = null
          false
        }
      }
    }

  inner class MyAsyncTask() :
    AsyncTask<Void?, Void?, Boolean>() {
    override fun doInBackground(vararg voidArr: Void?): Boolean {
      return if (this@AsyncCommand.onPreExecute()) {
        java.lang.Boolean.valueOf(this@AsyncCommand.execute())
      } else false
    }

    public override fun onPostExecute(bool: Boolean) {
      this@AsyncCommand.onPostExecute(bool)
      mListener?.onCommandFinished(bool)
    }
  }

}
