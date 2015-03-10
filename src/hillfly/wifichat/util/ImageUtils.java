package hillfly.wifichat.util;

import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.R;
import hillfly.wifichat.R.drawable;
import hillfly.wifichat.activity.imagefactory.ImageFactoryActivity;
import hillfly.wifichat.activity.imagefactory.ImageFactoryFliter.FilterType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @fileName PhotoUtils.java
 * @description 图片工具类
 */
public class ImageUtils {

    /** 相册的RequestCode **/
    public static final int INTENT_REQUEST_CODE_ALBUM = 0;
    /** 相机的RequestCode **/
    public static final int INTENT_REQUEST_CODE_CAMERA = 1;
    /** 裁剪照片的RequestCode **/
    public static final int INTENT_REQUEST_CODE_CROP = 2;
    /** 滤镜图片的RequestCode **/
    public static final int INTENT_REQUEST_CODE_FLITER = 3;

    /**
     * 通过手机相册获取图片
     * 
     * @param activity
     */
    public static void selectPhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, INTENT_REQUEST_CODE_ALBUM);
    }

    /**
     * 通过手机照相获取图片
     * 
     * @param activity
     * @return 照相后图片的路径
     */
    public static String takePicture(Activity activity) {
        FileUtils.createDirFile(BaseApplication.CAMERA_IMAGE_PATH);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = BaseApplication.CAMERA_IMAGE_PATH + UUID.randomUUID().toString() + "jpg";
        File file = FileUtils.createNewFile(path);
        if (file != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        activity.startActivityForResult(intent, INTENT_REQUEST_CODE_CAMERA);
        return path;
    }

    /**
     * 裁剪图片
     * 
     * @param context
     * @param activity
     * @param path
     *            需要裁剪的图片路径
     */
    public static void cropPhoto(Context context, Activity activity, String path) {
        Intent intent = new Intent(context, ImageFactoryActivity.class);
        if (path != null) {
            intent.putExtra("path", path);
            intent.putExtra(ImageFactoryActivity.TYPE, ImageFactoryActivity.CROP);
        }
        activity.startActivityForResult(intent, INTENT_REQUEST_CODE_CROP);
    }

    /**
     * 滤镜图片
     * 
     * @param context
     * @param activity
     * @param path
     *            需要滤镜的图片路径
     */
    public static void fliterPhoto(Context context, Activity activity, String path) {
        Intent intent = new Intent(context, ImageFactoryActivity.class);
        if (path != null) {
            intent.putExtra("path", path);
            intent.putExtra(ImageFactoryActivity.TYPE, ImageFactoryActivity.FLITER);
        }
        activity.startActivityForResult(intent, INTENT_REQUEST_CODE_FLITER);
    }

    /**
     * 从文件中获取图片
     * 
     * @param path
     *            图片的路径
     * @return
     */
    public static Bitmap getBitmapFromPath(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap getBitmapFromID(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 获取drawble中指定文件名的ID
     * 
     * @param 文件名
     * @return 文件对应ID
     * 
     */
    public static int getImageID(String imgName) {
        Class<drawable> draw = R.drawable.class;
        try {
            Field field = draw.getDeclaredField(imgName);
            return field.getInt(imgName);
        }
        catch (SecurityException e) {
            return R.drawable.app_icon;
        }
        catch (NoSuchFieldException e) {
            return R.drawable.app_icon;
        }
        catch (IllegalArgumentException e) {
            return R.drawable.app_icon;
        }
        catch (IllegalAccessException e) {
            return R.drawable.app_icon;
        }
    }

    /**
     * 创建图片缩略图
     * 
     * @param path
     *            图片路径
     * @param w
     *            宽度
     * @param h
     *            高度
     */
    public static void createThumbnail(Context context, String path, String dir) {
        int imagePX = dp2px(context, 100);
        savePhotoToSDCard(decodedBitmapFromPath(path, imagePX, imagePX), dir,
                FileUtils.getNameByPath(path));
    }

    /**
     * 缩放图片
     * 
     * @param path
     *            图片的路径
     * @param w
     *            宽度
     * @param h
     *            长度
     * @return
     */
    public static Bitmap decodedBitmapFromPath(String path, int reqWidth, int reqHeight) {
        try {

            // 首先设置 inJustDecodeBounds=true 来获取图片尺寸
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // 计算 inSampleSize 的值
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // 根据计算出的 inSampleSize 来解码图片生成Bitmap
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        }
        catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 计算缩放系数
     * 
     * @param options
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 获取图片的长度和宽度
     * 
     * @param bitmap
     *            图片bitmap对象
     * @return
     */
    public static Bundle getBitmapWidthAndHeight(Bitmap bitmap) {
        Bundle bundle = null;
        if (bitmap != null) {
            bundle = new Bundle();
            bundle.putInt("width", bitmap.getWidth());
            bundle.putInt("height", bitmap.getHeight());
            return bundle;
        }
        return null;
    }

    /**
     * 判断图片高度和宽度是否过大
     * 
     * @param bitmap
     *            图片bitmap对象
     * @return
     */
    public static boolean bitmapIsLarge(Bitmap bitmap) {
        final int MAX_WIDTH = 60;
        final int MAX_HEIGHT = 60;
        Bundle bundle = getBitmapWidthAndHeight(bitmap);
        if (bundle != null) {
            int width = bundle.getInt("width");
            int height = bundle.getInt("height");
            if (width > MAX_WIDTH && height > MAX_HEIGHT) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据比例缩放图片
     * 
     * @param screenWidth
     *            手机屏幕的宽度
     * @param filePath
     *            图片的路径
     * @param ratio
     *            缩放比例
     * @return
     */
    public static Bitmap CompressionPhoto(float screenWidth, String filePath, int ratio) {
        Bitmap bitmap = ImageUtils.getBitmapFromPath(filePath);
        Bitmap compressionBitmap = null;
        float scaleWidth = screenWidth / (bitmap.getWidth() * ratio);
        float scaleHeight = screenWidth / (bitmap.getHeight() * ratio);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            compressionBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        catch (Exception e) {
            return bitmap;
        }
        return compressionBitmap;
    }

    /**
     * 保存图片到SD卡
     * 
     * @param bitmap
     *            图片的bitmap对象
     */
    public static String savePhotoToSDCard(Bitmap bitmap, String filedir, String paramFilename) {

        if (!FileUtils.isSdcardExist()) {
            return null;
        }
        FileUtils.createDirFile(filedir);
        int quality = 60; // 缩略图没必要高质量
        String filename = paramFilename;

        if (TextUtils.isEmpty(paramFilename)) {
            filename = UUID.randomUUID().toString() + ".jpg";
            quality = 100;
        }

        String newFilePath = filedir + filename;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (IOException e1) {
            return null;
        }
        return newFilePath;
    }

    /**
     * 根据滤镜类型获取图片
     * 
     * @param filterType
     *            滤镜类型
     * @param defaultBitmap
     *            默认图片
     * @return
     */
    public static Bitmap getFilter(FilterType filterType, Bitmap defaultBitmap) {
        Bitmap returnBitmap = null;
        switch (filterType) {
            case 默认:
                returnBitmap = defaultBitmap;
                break;

            case LOMO:
                returnBitmap = lomoFilter(defaultBitmap);
                break;
        }
        return returnBitmap;
    }

    /**
     * 滤镜效果--LOMO
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap lomoFilter(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dst[] = new int[width * height];
        bitmap.getPixels(dst, 0, width, 0, 0, width, height);

        int ratio = width > height ? height * 32768 / width : width * 32768 / height;
        int cx = width >> 1;
        int cy = height >> 1;
        int max = cx * cx + cy * cy;
        int min = (int) (max * (1 - 0.8f));
        int diff = max - min;

        int ri, gi, bi;
        int dx, dy, distSq, v;

        int R, G, B;

        int value;
        int pos, pixColor;
        int newR, newG, newB;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos = y * width + x;
                pixColor = dst[pos];
                R = Color.red(pixColor);
                G = Color.green(pixColor);
                B = Color.blue(pixColor);

                value = R < 128 ? R : 256 - R;
                newR = (value * value * value) / 64 / 256;
                newR = (R < 128 ? newR : 255 - newR);

                value = G < 128 ? G : 256 - G;
                newG = (value * value) / 128;
                newG = (G < 128 ? newG : 255 - newG);

                newB = B / 2 + 0x25;

                // ==========边缘黑暗==============//
                dx = cx - x;
                dy = cy - y;
                if (width > height)
                    dx = (dx * ratio) >> 15;
                else
                    dy = (dy * ratio) >> 15;

                distSq = dx * dx + dy * dy;
                if (distSq > min) {
                    v = ((max - distSq) << 8) / diff;
                    v *= v;

                    ri = (int) (newR * v) >> 16;
                    gi = (int) (newG * v) >> 16;
                    bi = (int) (newB * v) >> 16;

                    newR = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
                    newG = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
                    newB = bi > 255 ? 255 : (bi < 0 ? 0 : bi);
                }
                // ==========边缘黑暗end==============//

                dst[pos] = Color.rgb(newR, newG, newB);
            }
        }

        Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);
        return acrossFlushBitmap;
    }

    /**
     * 获取圆角图片
     * 
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获取颜色的圆角bitmap
     * 
     * @param context
     * @param color
     * @return
     */
    public static Bitmap getRoundBitmap(Context context, int color) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.0f,
                metrics));
        int height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f,
                metrics));
        int round = Math.round(TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f, metrics));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(new RectF(0.0F, 0.0F, width, height), round, round, paint);
        return bitmap;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
