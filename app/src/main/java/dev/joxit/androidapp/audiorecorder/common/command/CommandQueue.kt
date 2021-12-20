package dev.joxit.androidapp.audiorecorder.common.command

import android.content.Context
import java.util.concurrent.atomic.AtomicBoolean


class CommandQueue(
  context: Context,
  synchronous: Boolean,
  commandQueueListener: CommandQueueListener?
) {
  private val mAllowCommands: AtomicBoolean = AtomicBoolean(true)
  private val mExecutor: CommandExecutor = CommandExecutor(context.applicationContext)
  private val mListener: CommandQueueListener? = commandQueueListener
  private val mPaused: AtomicBoolean = AtomicBoolean(false)
  private val mReadyCommands: MutableList<Command> = mutableListOf()
  private val mRunning: AtomicBoolean = AtomicBoolean(false)
  private val mSynchronous: Boolean = synchronous

  interface Command {
    fun onFinish(z: Boolean)
    fun run(): Boolean
  }

  interface CommandQueueListener {
    fun onEmptyQueue(z: Boolean)
  }

  fun add(command: Command) {
    synchronized(mReadyCommands) {
      if (mAllowCommands.get()) {
        mReadyCommands.add(command)
        updateQueue()
      }
    }
  }

  fun clearAndAdd(command: Command) {
    synchronized(mReadyCommands) {
      if (mAllowCommands.get()) {
        mReadyCommands.clear()
        mReadyCommands.add(command)
        updateQueue()
      }
    }
  }

  fun pause() {
    mPaused.set(true)
  }

  fun resume() {
    mPaused.set(false)
    updateQueue()
  }

  fun init() {
    mAllowCommands.set(true)
  }

  fun shutdown() {
    if (mAllowCommands.get()) {
      mAllowCommands.set(false)
      updateQueue()
    }
  }

  val isInited: Boolean
    get() = mAllowCommands.get()

  private fun start() {
    synchronized(mReadyCommands) {
      mExecutor.setCommand(mReadyCommands.removeAt(0))
      mRunning.set(true)
      if (mSynchronous) {
        mExecutor.startSync()
      } else {
        mExecutor.start()
      }
    }
  }

  private fun updateQueue() {
    synchronized(mReadyCommands) {
      if (!mRunning.get() && !mPaused.get()) {
        if (mReadyCommands.size > 0) {
          start()
        } else {
          mListener?.onEmptyQueue(mAllowCommands.get())
        }
      }
    }
  }

  inner class CommandExecutor(context: Context) :
    AsyncCommand(context, null) {
    private var mCommand: Command? = null

    override fun execute(): Boolean {
      Thread.currentThread().name = "Command Queue"
      val command = mCommand
      return command != null && command.run()
    }

    override fun onPostExecute(z: Boolean) {
      mCommand!!.onFinish(z)
      mRunning.set(false)
      updateQueue()
    }

    fun setCommand(command: Command?) {
      mCommand = command
    }
  }

  init {
    RuntimeException(this.toString()).printStackTrace()
  }
}
