package com.stardust.autojs.engine;

import android.support.annotation.CallSuper;

import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.script.ScriptSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2017/4/2.
 * <p>
 * <p>
 * A ScriptEngine is created by {@link ScriptEngineManager#createEngine(String)} ()}, and then can be
 * used to execute script with {@link ScriptEngine#execute(ScriptSource)} in the **same** thread.
 * When the execution finish successfully, the engine should be destroy in the thread that created it.
 * <p>
 * If you want to stop the engine in other threads, you should call {@link ScriptEngine#forceStop()}.
 * It will throw a {@link ScriptException}.
 */

public interface ScriptEngine<S extends ScriptSource> {


    String TAG_PATH = "execute_path";
    String TAG_SOURCE = "source";

    void put(String name, Object value);

    Object execute(S scriptSource);

    void forceStop();

    void destroy();

    boolean isDestroyed();

    void setTag(String key, Object value);

    Object getTag(String key);

    /**
     * @hide
     */
    void setOnDestroyListener(OnDestroyListener listener);

    /**
     * @hide
     */
    void init();

    interface OnDestroyListener {
        void onDestroy(ScriptEngine engine);
    }

    abstract class AbstractScriptEngine<S extends ScriptSource> implements ScriptEngine<S> {


        private Map<String, Object> mTags = new ConcurrentHashMap<>();
        private OnDestroyListener mOnDestroyListener;
        private boolean mDestroyed = false;


        @Override
        public synchronized void setTag(String key, Object value) {
            mTags.put(key, value);
        }

        @Override
        public synchronized Object getTag(String key) {
            return mTags.get(key);
        }

        @Override
        public synchronized boolean isDestroyed() {
            return mDestroyed;
        }

        @CallSuper
        @Override
        public synchronized void destroy() {
            mDestroyed = true;
            if (mOnDestroyListener != null) {
                mOnDestroyListener.onDestroy(this);
            }
        }

        public String cwd() {
            return (String) getTag(TAG_PATH);
        }

        public void setOnDestroyListener(OnDestroyListener onDestroyListener) {
            if (mOnDestroyListener != null)
                throw new SecurityException("setOnDestroyListener can be called only once");
            mOnDestroyListener = onDestroyListener;
        }
    }
}
