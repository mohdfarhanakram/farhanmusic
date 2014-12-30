package com.pointburst.jsmusic.utils;

import android.content.Context;
import com.squareup.picasso.Picasso;

/**
 * Created by m.farhan on 12/30/14.
 */
public class PicassoEx {
    private static Picasso mPicasso;

    public static Picasso getPicasso(Context context) {
        if (null == mPicasso) {
            //   Picasso.Builder builder = new Picasso.Builder(context);
            //  builder.executor(PicassoThreadExecutor.getThreadPoolExecutor());
            //  mPicasso = builder.build();
            mPicasso=Picasso.with(context);
            //mPicasso.setDebugging(true);
            //   mPicasso.setLoggingEnabled(true);
            //  mPicasso.setIndicatorsEnabled(true);
        }
        return mPicasso;
    }

}
