/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.cadutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Xml;
import android.webkit.MimeTypeMap;

import com.debugTools.debugTool;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 纯的文件相关的操作
 * <p/>
 * 创建时间: 2014-8-19 下午2:30:18 <br/>
 * 
 * @author xnjiang
 * @version
 * @since v0.0.1
 */
@SuppressLint("DefaultLocale")
public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();
	public final static String FILENAME = "fileName";
	public final static String FILEPATH = "filePath";
	public final static String FILEDATE = "fileDate";
	public final static String FILESIZE = "fileSize";
	public final static String FILETYPE = "fileType";

	// 果您需要往sdcard中保存特定类型的内容，
	// 可以考虑使用Environment.getExternalStoragePublicDirectory(String type)函数，
	// 该函数可以返回特定类型的目录，目前支持如下类型：
	// Environment.DIRECTORY_ALARMS //警报的铃声
	// Environment.DIRECTORY_DCIM //相机拍摄的图片和视频保存的位置
	// Environment.DIRECTORY_DOWNLOADS //下载文件保存的位置
	// Environment.DIRECTORY_MOVIES //电影保存的位置， 比如 通过google play下载的电影
	// Environment.DIRECTORY_MUSIC //音乐保存的位置
	// Environment.DIRECTORY_NOTIFICATIONS //通知音保存的位置
	// Environment.DIRECTORY_PICTURES //下载的图片保存的位置
	// Environment.DIRECTORY_PODCASTS //用于保存podcast(博客)的音频文件
	// Environment.DIRECTORY_RINGTONES //保存铃声的位置
	/*
	 * //Android 内存卡 文件夹 API File cachef=this.getExternalCacheDir();//获取缓存目录
	 * 程序卸载后自动删除 File
	 * file1=this.getExternalFilesDir(Environment.DIRECTORY_DCIM);
	 * //相机拍摄的图片和视频保存的位置 File
	 * file2=this.getExternalFilesDir(Environment.DIRECTORY_ALARMS); //警报的铃声
	 * File
	 * file3=this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);//下载文件保存的位置
	 * File file4=this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
	 * //电影保存的位置 File
	 * file5=this.getExternalFilesDir(Environment.DIRECTORY_MUSIC); //音乐保存的位置
	 * File
	 * file6=this.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);/
	 * /通知音保存的位置 File
	 * file7=this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	 * //下载的图片保存的位置 File
	 * file8=this.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
	 * //用于保存(博客)的音频文件 File
	 * file9=this.getExternalFilesDir(Environment.DIRECTORY_RINGTONES);//保存铃声的位置
	 * File f=Environment.getDataDirectory();//获取 Android 数据目录 File
	 * f2=Environment.getDownloadCacheDirectory();//获取 Android 下载/缓存内容目录 File
	 * f3=Environment.getExternalStorageDirectory();//sdcard路径 常用 File
	 * f4=Environment.getExternalStoragePublicDirectory("");//同
	 * this.getExternalFilesDir(...)
	 */
	/**
	 * (所有文件扫瞄)扫描完成后的回调，获取文件列表必须实现
	 */
	public interface OnFileListAllCallback {
		/**
		 * 返回查询的文件列表(所有文件信息，不含目录信息)
		 * 
		 * @param list
		 *            文件列表 ，文件信息包含（fileName，filePath）
		 */
		public void SearchFileListAll(List<Map<String, Object>> list);
	}

	/**
	 * (当前目录层级扫瞄)扫描完成后的回调，获取文件列表必须实现
	 */
	public interface OnFileListCurrentLevelCallback {
		/**
		 * 返回当前文件路径下的文件和目录
		 * 
		 * @param list
		 *            文件列表 ，文件信息包含（fileName，filePath）
		 */
		public void SearchFileListCurrentLevel(List<Map<String, Object>> list);
	}

	/**
	 * 获取文件中的视频信息
	 * 
	 * @author xnjiang
	 * @param list
	 * @param file
	 * @since v0.0.1
	 */
	public void getVideoFile(final List<Map<String, Object>> list, File fileRoot) {// 获得视频文件
		if (fileRoot == null || list == null) {
			return;
		}
		try {
			fileRoot.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					// sdCard找到视频名称
					String name = file.getName();

					int i = name.indexOf('.');
					if (i != -1) {
						name = name.substring(i);
						if (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv") || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".rmvb") || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v") || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8") || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2") || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv") || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v") || name.equalsIgnoreCase(".rm") || name.equalsIgnoreCase(".asf") || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg") || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf") || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
								|| name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx") || name.equalsIgnoreCase(".xvid")) {
							Map<String, Object> vi = new HashMap<String, Object>();
							vi.put("name", file.getName());
							vi.put("path", file.getAbsolutePath());
							list.add(vi);
							return true;
						}
					} else if (file.isDirectory()) {
						getVideoFile(list, file);
					}
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件是否可以显示（过滤没有权限的文件和以 点(.)开头的文件）
	 * 
	 * @Author StoneJxn
	 * @Date 2016年6月3日 上午10:56:53
	 * @param file
	 * @return
	 */
	public static boolean checkFileModelPermissionShow(Context context, File file) {
		try {
			if (Build.VERSION.SDK_INT >= 23 && FileUtils.isCompareFiles(file.getParent(), getStorageRootPath(context))) {
				File fileSDCardInner = new File(getAvailableFilesPathSystem(context));
				if (FileUtils.isCompareFiles(fileSDCardInner, file)) {
					return false;
				}
			}
			// 过滤没有权限的文件和以 点(.)开头的文件
			int intFilePermission = getFilePermission(file.getPath());
			// 6.0系统特殊处理,只读文件也显示
			if (Build.VERSION.SDK_INT >= 23) {
				intFilePermission = intFilePermission > 0 ? intFilePermission : intFilePermission++;
			}
			if (intFilePermission <= 0 || file.getName().startsWith(".")) {// 加载不是私有的目录
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取文件列表信息,给定目录下的这一级文件列表
	 * 
	 * @方法名称: getFileListAll
	 * @创建人: StoneJxn
	 * @创建时间: 2016年1月5日下午4:23:10
	 * @参数: @param fileRoot
	 * @参数: @param listFileModels
	 * @参数: @param strFileName
	 * @返回值: void
	 */
	public static void getFileModelListCurrent(final Context context, File fileRoot, final List<FileModel> listFileModels) {
		if (fileRoot == null || listFileModels == null) {
			return;
		}
		try {
			// 因为6.0以上系统，访问根节点是从/storager开始，但是它里面直接获取不到内置sdcard0，所需需要手动添加
			if (Build.VERSION.SDK_INT >= 23 && isStorageRootPath(context, fileRoot.getPath()) && FileUtils.isCompareFiles(fileRoot, new File("/storage"))) {
				File fileSDCardInner = new File(getAvailableFilesPathSystem(context));

				FileModel fileData = new FileModel();
				long fileDate = fileSDCardInner.lastModified();
				Date date = new Date(fileDate);
				String formatDate = date.toString();
				long fileSize = getFileSize(fileSDCardInner, false);
				String formatSize = formatFileSize(fileSize);
				String fileType = getFileExtensionNoPoint(fileSDCardInner);

				fileData.setFileName(fileSDCardInner.getName());
				fileData.setFilePath(fileSDCardInner.getPath());
				fileData.setFileDate(fileDate);
				fileData.setFileDateShow(formatDate);
				fileData.setFileSize(fileSize);
				fileData.setFileSizeShow(formatSize);
				fileData.setFileType(fileType);
				fileData.setFile(fileSDCardInner.isFile());
				fileData.setDirectory(fileSDCardInner.isDirectory());
				fileData.setFileSelected(false);
				listFileModels.add(fileData);
			}
			fileRoot.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					// 过滤没有权限的文件和以 点(.)开头的文件
					if (!checkFileModelPermissionShow(context, file)) {
						return false;
					}
//					// 过滤不支持的的文件类型
//					if (file.isFile()) {
//						if (!GstarSDK.getInstance(context).isSupportFileType_Dwg(file.getName())) {
//							return false;
//						}
//					}
					FileModel fileData = new FileModel();
					long fileDate = file.lastModified();
					Date date = new Date(fileDate);
					String formatDate = date.toString();
					;
					long fileSize = getFileSize(file, false);
					String formatSize = formatFileSize(fileSize);
					String fileType = getFileExtensionNoPoint(file);

					fileData.setFileName(file.getName());
					fileData.setFilePath(file.getPath());
					fileData.setFileDate(fileDate);
					fileData.setFileDateShow(formatDate);
					fileData.setFileSize(fileSize);
					fileData.setFileSizeShow(formatSize);
					fileData.setFileType(fileType);
					fileData.setFile(file.isFile());
					fileData.setDirectory(file.isDirectory());
					fileData.setFileSelected(false);

					listFileModels.add(fileData);
					return true;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 内部测试使用代码
	 * 
	 * @Author StoneJxn
	 * @Date 2016年6月2日 下午3:42:20
	 * @param fileRoot
	 * @param listFileModels
	 */
	public static void getFileModelListCurrentDebug(File fileRoot, final List<FileModel> listFileModels) {
		if (fileRoot == null || listFileModels == null) {
			return;
		}
		try {
			fileRoot.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					FileModel fileData = new FileModel();
					long fileDate = file.lastModified();
					Date date = new Date(fileDate);
					String formatDate = date.toString();
					;
					long fileSize = getFileSize(file, false);
					String formatSize = formatFileSize(fileSize);
					String fileType = getFileExtensionNoPoint(file);

					fileData.setFileName(file.getName());
					fileData.setFilePath(file.getPath());
					fileData.setFileDate(fileDate);
					fileData.setFileDateShow(formatDate);
					fileData.setFileSize(fileSize);
					fileData.setFileSizeShow(formatSize);
					fileData.setFileType(fileType);
					fileData.setFile(file.isFile());
					fileData.setDirectory(file.isDirectory());
					fileData.setFileSelected(false);

					listFileModels.add(fileData);
					return true;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 本地文件数组排序功能
	 * 
	 * @param list
	 *            排序数据
	 * @param sortKey
	 *            排序字段或者说是种类名称（如：日期，文件名，文件大小，文件类型）
	 * @param order
	 *            排序方式 true 升序 false 降序
	 */
	public static void sortFileModelList(List<FileModel> list, final String sortKey, final boolean order) {
		if (list == null || list.size() < 1 || TextUtils.isEmpty(sortKey)) {
			return;
		}
		try {
			Collections.sort(list, new Comparator<FileModel>() {
				@Override
				public int compare(FileModel o1, FileModel o2) {
					if (TextUtils.isEmpty(sortKey)) {
						return 0;
					}
					if (sortKey.equalsIgnoreCase("fileName")) {
						String str1 = o1.getFileName();
						String str2 = o2.getFileName();
						if (order) {
							return str1.compareToIgnoreCase(str2);
						} else {
							return str2.compareToIgnoreCase(str1);
						}
					} else if (sortKey.equalsIgnoreCase("fileType")) {
						String str1 = o1.getFileType();
						String str2 = o2.getFileType();
						if (order) {
							return str1.compareToIgnoreCase(str2);
						} else {
							return str2.compareToIgnoreCase(str1);
						}
					} else if (sortKey.equalsIgnoreCase("fileDate")) {
						Long lng1 = o1.getFileDate();
						Long lng2 = o2.getFileDate();
						if (order) {
							return lng1.compareTo(lng2);
						} else {
							return lng2.compareTo(lng1);
						}
					} else if (sortKey.equalsIgnoreCase("fileSize")) {
						Long lng1 = o1.getFileSize();
						Long lng2 = o2.getFileSize();
						if (order) {
							return lng1.compareTo(lng2);
						} else {
							return lng2.compareTo(lng1);
						}
					}
					return 0;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件过滤器
	 * 
	 * @author Stone.J
	 */
	public static class ExtensionFileFilter implements FileFilter {
		private String isShow;
		private String strKeyword;
		private String[] extensions;

		/**
		 * 
		 * @param isShow
		 *            show添加 hide排除
		 * @param strings
		 *            过滤的后缀名如：.png、.jpg
		 */
		public ExtensionFileFilter(String isShow, String strKeyword, String... extensions) {
			this.isShow = isShow;
			strKeyword = strKeyword;
			this.extensions = extensions;
		}

		@Override
		public boolean accept(File pathname) {
			String strFileName = pathname.getName();
			if (pathname.isDirectory()) {
				return true;
			} else {
				if (extensions == null || extensions.length == 0) {
					return true;
				} else {
					if (isShow.equalsIgnoreCase("show")) {
						for (String str : extensions) {
							if (strFileName.toLowerCase(Locale.getDefault()).endsWith(str.toLowerCase(Locale.getDefault()))) {
								if (!TextUtils.isEmpty(strKeyword)) {
									if (strFileName.toLowerCase(Locale.getDefault()).contains(strKeyword.toLowerCase(Locale.getDefault()).trim())) {
										return true;
									}
								} else {
									return true;
								}
							}
						}
						return false;
					} else {
						for (String str : extensions) {
							if (strFileName.toLowerCase(Locale.getDefault()).endsWith(str.toLowerCase(Locale.getDefault()))) {
								if (!TextUtils.isEmpty(strKeyword)) {
									if (strFileName.toLowerCase(Locale.getDefault()).contains(strKeyword.toLowerCase(Locale.getDefault()).trim())) {
										return false;
									}
								} else {
									return false;
								}
							}
						}
						return true;
					}
				}
			}
		}
	}

	public static class FileComparator implements Comparator<Object> {
		private String sortKey;
		private boolean sortOrder;

		/**
		 * 排序比较器
		 * 
		 * @param sortKey
		 *            排序字段
		 * @param order
		 *            排序方式 true 升序 false 降序
		 */
		public FileComparator(String sortKey, boolean sortOrder) {
			this.sortKey = sortKey;
			this.sortOrder = sortOrder;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			if (TextUtils.isEmpty(sortKey)) {
				return -1;
			}
			if (o1 instanceof Map) {
				Map<String, Object> map1 = (Map<String, Object>) o1;
				Map<String, Object> map2 = (Map<String, Object>) o2;
				if (!map1.containsKey(sortKey) || !map2.containsKey(sortKey)) {
					return -1;
				}
				if (sortOrder) {
					return map1.get(sortKey).toString().compareToIgnoreCase(map2.get(sortKey).toString());
				} else {
					return map2.get(sortKey).toString().compareToIgnoreCase(map1.get(sortKey).toString());
				}
			} else if (o1 instanceof String) {
				String str1 = (String) o1;
				String str2 = (String) o2;
				if (sortOrder) {
					return str1.compareToIgnoreCase(str2);
				} else {
					return str2.compareToIgnoreCase(str1);
				}
			} else if (o1 instanceof File) {
				File file1 = (File) o1;
				File file2 = (File) o2;
				String filename1 = file1.getName();
				String filename2 = file2.getName();
				if (sortOrder) {
					return filename1.compareToIgnoreCase(filename2);
				} else {
					return filename2.compareToIgnoreCase(filename1);
				}
			}
			return -1;
		}

	}

	/**
	 * 数组排序功能
	 * 
	 * @param list
	 *            排序数据
	 * @param sortKey
	 *            排序字段
	 * @param order
	 *            排序方式 true 升序 false 降序
	 */
	public static void sortArrayList(List<?> list, final String sortKey, final boolean order) {
		if (list == null || list.size() < 1 || TextUtils.isEmpty(sortKey)) {
			return;
		}
		try {
			Collections.sort(list, new Comparator<Object>() {
				@Override
				@SuppressWarnings("unchecked")
				public int compare(Object o1, Object o2) {
					if (TextUtils.isEmpty(sortKey)) {
						return -1;
					}
					if (o1 instanceof Map) {
						Map<String, Object> map1 = (Map<String, Object>) o1;
						Map<String, Object> map2 = (Map<String, Object>) o2;
						if (!map1.containsKey(sortKey) || !map2.containsKey(sortKey)) {
							return -1;
						}
						if (order) {
							return map1.get(sortKey).toString().compareToIgnoreCase(map2.get(sortKey).toString());
						} else {
							return map2.get(sortKey).toString().compareToIgnoreCase(map1.get(sortKey).toString());
						}
					} else if (o1 instanceof String) {
						String str1 = (String) o1;
						String str2 = (String) o2;
						if (order) {
							return str1.compareToIgnoreCase(str2);
						} else {
							return str2.compareToIgnoreCase(str1);
						}
					} else if (o1 instanceof File) {
						File file1 = (File) o1;
						File file2 = (File) o2;
						String filename1 = file1.getName();
						String filename2 = file2.getName();
						if (order) {
							return filename1.compareToIgnoreCase(filename2);
						} else {
							return filename2.compareToIgnoreCase(filename1);
						}
					}
					return -1;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合并两个数组对象成为一个数组对象
	 * 
	 * @param <T>
	 * @param a1
	 * @param a2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> combineArrayList(List<T> a1, List<T> a2) {
		for (Object obj : a2) {
			a1.add((T) obj);
		}
		return a1;
	}

	/**
	 * 调用系统功能重新扫描指定的文件夹,写入系统媒体数据库
	 * 
	 * @author xnjiang
	 * @param context
	 * @param strFileName
	 * @since v0.0.1
	 */
	public static void scanMediaFileDataBase(Context context, String strFileName) {
		// 通常在我们的项目中，可能会遇到写本地文件，最常用的就是图片文件，在这之后需要通知系统重新扫描SD卡，
		// 在Android4.4之前也就是以发送一个Action为“Intent.ACTION_MEDIA_MOUNTED”的广播通知执行扫描。如下：

		// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://" + strRefreshDir)));

		// 但在Android4.4中，则会抛出以下异常：
		// W/ActivityManager( 498): Permission Denial: not allowed to send
		// broadcast android.intent.action.MEDIA_MOUNTED from pid=2269,
		// uid=20016
		// 那是因为Android4.4中限制了系统应用才有权限使用广播通知系统扫描SD卡。
		// 解决方式：
		// 使用MediaScannerConnection执行具体文件或文件夹进行扫描。
		// MediaScannerConnection.scanFile(context, new
		// String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
		// + "/" + strFileName}, null, null);

		// 判断目录如果是文件，就获取其上一级路径也进行刷新
		String strFileParent = new File(strFileName).isFile() ? new File(strFileName).getParentFile().getPath() : strFileName;
		MediaScannerConnection.scanFile(context, new String[] { strFileName, strFileParent }, null, null);
	}

	/**
	 * 判断是否是存储设备跟节点
	 * 
	 * @Author StoneJxn
	 * @Date 2016年5月4日 下午2:11:22
	 * @param context
	 * @param strFilePath
	 * @return
	 */
	public static boolean isStorageRootPath(Context context, String strFilePath) {
		if (TextUtils.isEmpty(strFilePath)) {
			return true;
		}
		if (FileUtils.isCompareFiles(strFilePath, FileUtils.getStorageRootPath(context))) {
			return true;
		}
		return false;
	}

	/**
	 * 获取存储设备跟上一个节点路径
	 * 
	 * @Author StoneJxn
	 * @Date 2016年5月4日 下午2:11:22
	 * @param context
	 * @param strFilePath
	 * @return
	 */
	public static String getStorageBackPath(Context context, String strFilePath) {
		if (!TextUtils.isEmpty(strFilePath)) {
			File fileNow = new File(strFilePath);
			if (FileUtils.isCompareFiles(fileNow, new File(getAvailableFilesPathSystem(context)))) {
				return FileUtils.getStorageRootPath(context);
			}
			// 获得父类的目录
			if (fileNow != null && fileNow.getParent() != null && !fileNow.getParent().equals("/")) {
				return fileNow.getParent();
			}
		}
		return FileUtils.getStorageRootPath(context);
	}

	/**
	 * 获取可用存储器列表的 根节点
	 * 
	 * @方法名称: getAvailableStorageList
	 * @创建人: StoneJxn
	 * @创建时间: 2015年10月22日下午4:35:30
	 * @参数: @param context
	 * @参数: @return
	 * @返回值: List<String>
	 */
	public static String getStorageRootPath(Context context) {
		// // 6.0系统特殊处理
		// if (Build.VERSION.SDK_INT >= 23) {
		// return getAvailableFilesPathSystem(context);
		// }
		// String root = getAvailableFilesPathSystem(context);
		// List<String> listPathRoot = getAvailableStorageList(context);
		// if (listPathRoot != null && listPathRoot.size() > 0) {
		// String pathFirst = listPathRoot.get(0);
		// String pathLast = listPathRoot.get(listPathRoot.size() - 1);
		// if (pathFirst.trim().length() < pathLast.trim().length()) {
		// root = new File(pathFirst).getParentFile().getPath();
		// } else {
		// root = new File(pathLast).getParentFile().getPath();
		// }
		// if (root.indexOf("emulated") >= 0) {
		// root = new File(root).getParentFile().getPath();
		// }
		// }

		return "/storage";
	}

	/**
	 * 检测SD卡是否存在
	 */
	public static boolean isSDExist() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 多个SD卡存在时 获取外置SD卡路径<br>
	 *
	 * @return
	 */
	public static String getExternalStorageDirectory() {
		Map<String, String> map = System.getenv();
		String[] values = new String[map.values().size()];
		map.values().toArray(values);
		String path = values[values.length - 1]; // 外置SD卡的路径
		if (path.startsWith("/mnt/") && !Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
			return path;
		} else {
			return null;
		}
	}

	/**
	 * 获取有效的文件目录（SD卡根目录、程序根目录）<br>
	 * 如果SD卡存在--返回SD根目录 <br>
	 * 如果SD卡不存在 --返回程序根目录
	 *
	 * @return e.g. “/storage/sdcard0/” 或 “/data/data/{package name}/files/”
	 */
	public static String getAvailableFilesPathSystem(Context context) {
		if (!isSDExist()) {
			return getSystemFilesPath(context);
		}
		return getSDCardFilesPath();
	}

	/**
	 * 获取SD卡根目录路径
	 *
	 * @return e.g. /storage/sdcard0/
	 */
	public static String getSDCardFilesPath() {
		if (!isSDExist()) {
			return null;
		}
		return Environment.getExternalStorageDirectory().getPath() + "/";
	}

	/**
	 * 获取SD卡根目下的下载目录路径
	 *
	 * @return e.g. /storage/sdcard0/Download/
	 */
	public static String getSDCardDownloadPath() {
		if (!isSDExist()) {
			return null;
		}
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/";
	}

	/**
	 * 获取SD卡根目下的缓存目录路径
	 *
	 * @return e.g. /storage/sdcard0/Download/cache/
	 */
	public static String getSDCardCachePath() {
		if (!isSDExist()) {
			return null;
		}
		return Environment.getDownloadCacheDirectory().getPath() + "/";
	}

	/**
	 * 私有文件目录路径
	 *
	 * @param ctx
	 * @return /data/data/{package name}/files/
	 */
	public static String getSystemFilesPath(Context ctx) {
		return ctx.getFilesDir().getPath() + "/";
	}

	/**
	 * 系统文件中的缓存目录路径
	 *
	 * @param ctx
	 * @return /data/data/{package name}/cache/
	 */
	public static String getSystemCachePath(Context ctx) {
		return ctx.getCacheDir().getPath() + "/";
	}

	/**
	 * 删除一个文件
	 *
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		if (file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 删除一个文件
	 *
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		return deleteFile(new File(path));
	}

	/**
	 * 删除一个目录（可以是非空目录）
	 *
	 * @param dir
	 */
	public static boolean deleteDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				deleteDir(file);// 递归
			}
		}
		dir.delete();
		return true;
	}

	/**
	 * 删除一个目录（可以是非空目录）
	 *
	 * @param path
	 */
	public static boolean deleteDir(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		return deleteDir(new File(path));
	}

	/**
	 * 删除一个目录或者文件（可以是非空目录）
	 *
	 * @param file
	 */
	public static boolean deleteDirOrFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		return deleteDirOrFile(new File(path));
	}

	/**
	 * 删除一个目录或者文件（可以是非空目录）
	 *
	 * @param file
	 */
	public static boolean deleteDirOrFile(File file) {
		if (file == null || !file.exists()) {
			return false;
		}
		if (file.isDirectory()) {// 目录
			return deleteDir(file);
		} else {// 文件
			return deleteFile(file);
		}
	}

	/**
	 * 拷贝一个文件,srcFile源文件，destFile目标文件
	 *
	 * @param srcFile
	 *            文件、目录
	 * @param destFile
	 *            文件、目录
	 * @return
	 */
	public static boolean copyFileTo(File srcFile, File destFile) {
		if (!srcFile.exists())
			return false;// 判断是否存在
		File inputFile = srcFile;
		File outputFile = destFile;
		if (srcFile.isDirectory()) {
			if (!destFile.isDirectory()) {
				return false;
			}
			String newPath = destFile.getPath() + "/" + srcFile.getName();
			createDir(newPath);
			copyFilesTo(srcFile.getPath(), newPath);
		}
		if (destFile.isDirectory() && destFile.exists()) {
			outputFile = new File(destFile.getPath() + "/" + srcFile.getName());
		} else if (destFile.getParent() == null) {
			return false;
		}
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(outputFile);
			int readLen = 0;
			byte[] buf = new byte[4096];
			while ((readLen = fis.read(buf)) != -1) {
				fos.write(buf, 0, readLen);
			}
			fos.flush();
			fos.close();
			fis.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 拷贝一个文件,srcFile源文件，destFile目标文件
	 *
	 * @param srcPath
	 *            文件、目录的路径
	 * @param destPath
	 *            文件、目录的路径
	 * @return
	 */
	public static boolean copyFileTo(String srcPath, String destPath) {
		return copyFileTo(new File(srcPath), new File(destPath));
	}

	/**
	 * 拷贝目录下的所有文件到指定目录
	 *
	 * @param srcDir
	 *            完整目录
	 * @param destDir
	 *            完整目录
	 * @return
	 */
	public static boolean copyFilesTo(File srcDir, File destDir) {
		if (!srcDir.exists() || !destDir.exists())
			return false;// 判断是否存在
		if (!srcDir.isDirectory() || !destDir.isDirectory())
			return false;// 判断是否是目录
		try {
			File[] srcFiles = srcDir.listFiles();
			for (int i = 0; i < srcFiles.length; i++) {
				if (srcFiles[i].isFile()) {
					// 获得目标文件
					File destFile = new File(destDir.getPath() + "//" + srcFiles[i].getName());
					copyFileTo(srcFiles[i], destFile);
				} else if (srcFiles[i].isDirectory()) {
					File theDestDir = new File(destDir.getPath() + "//" + srcFiles[i].getName());
					copyFilesTo(srcFiles[i], theDestDir);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 拷贝目录下的所有文件到指定目录
	 *
	 * @param srcPath
	 *            完整目录的路径
	 * @param destPath
	 *            完整目录的路径
	 * @return
	 */
	public static boolean copyFilesTo(String srcPath, String destPath) {
		return copyFilesTo(new File(srcPath), new File(destPath));
	}

	/**
	 * 移动一个文件、目录到一个新的文件夹
	 *
	 * @param srcFile
	 *            文件、目录
	 * @param destFile
	 *            完整目录
	 */
	public static boolean moveFileTo(File srcFile, File destFile) {
		// File (or directory) to be moved
		if (!srcFile.exists()) {
			return false;
		}
		// Destination directory
		if (!destFile.exists() || !destFile.isDirectory()) {
			return false;
		}
		// Move file to new directory
		boolean success = srcFile.renameTo(new File(destFile, srcFile.getName()));

		return success;
	}

	/**
	 * 移动一个文件、目录到一个新的文件夹
	 *
	 * @param srcPath
	 *            文件、目录的路径
	 * @param destPath
	 *            完整目录的路径
	 */
	public static boolean moveFileTo(String srcPath, String destPath) {
		return moveFileTo(new File(srcPath), new File(destPath));
	}

	/**
	 * 移动目录下的所有文件到指定目录
	 *
	 * @param srcDir
	 *            完整目录
	 * @param destDir
	 *            完整目录
	 * @return
	 */
	public static boolean moveFilesTo(File srcDir, File destDir) {
		if (srcDir == null || !srcDir.isDirectory() || destDir == null || !destDir.isDirectory()) {
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for (int i = 0; i < srcDirFiles.length; i++) {
			moveFileTo(srcDirFiles[i], destDir);
		}
		return true;
	}

	/**
	 * 移动目录下的所有文件到指定目录
	 *
	 * @param srcPath
	 *            完整目录的路径
	 * @param destPath
	 *            完整目录的路径
	 * @return
	 */
	public static boolean moveFilesTo(String srcPath, String destPath) {
		return moveFilesTo(new File(srcPath), new File(destPath));
	}

	/**
	 * 给文件或目录重命名
	 *
	 * @param targetFile
	 *            文件、目录
	 * @param newFileName
	 *            新的文件名字（单个文件要记得加上后缀.xxx）
	 * @return
	 */
	public static String renameFile(File targetFile, String newFileName) {
		if (targetFile == null || !targetFile.exists() || targetFile.getParentFile() == null) {
			return "";
		}
		if (TextUtils.isEmpty(newFileName)) {
			return targetFile.getPath();
		}
		if (targetFile.renameTo(new File(targetFile.getParentFile(), newFileName))) {
			return targetFile.getParentFile() + File.separator + newFileName;
		} else {
			return "";
		}
	}

	/**
	 * 给文件或目录重命名
	 *
	 * @param targetPath
	 *            文件、目录的路径
	 * @param newFileName
	 *            新的文件名字（单个文件要记得加上后缀.xxx）
	 * @return
	 */
	public static String renameFile(String targetPath, String newFileName) {
		return renameFile(new File(targetPath), newFileName);
	}

	/**
	 * 将二进制流写入文件
	 *
	 * @param path
	 * @param is
	 * @return
	 */
	public static boolean writeFileFromStream(String path, InputStream is) {
		boolean result = false;
		FileOutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			File file = new File(path);
			os = new FileOutputStream(file, false);
			bos = new BufferedOutputStream(os);
			int readLen = 0;
			byte[] buf = new byte[4096];
			while ((readLen = is.read(buf)) != -1) {
				bos.write(buf, 0, readLen);
			}
			bos.flush();
			bos.close();
			os.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 将字符串写入文件
	 *
	 * @param path
	 * @param data
	 * @return
	 */
	public static boolean writeFileFromString(String path, String data) {
		boolean result = false;
		FileWriter fw = null;
		try {
			File file = new File(path);
			fw = new FileWriter(file);
			fw.write(data);
			fw.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 创建目录文件夹
	 *
	 * @Author StoneJxn
	 * @Date 2016年2月25日 上午9:15:58
	 * @param path
	 * @return
	 */
	public static boolean createDir(String path) {
		if (!TextUtils.isEmpty(path)) {
			File newFileDir = new File(path);
			if (!newFileDir.exists()) {
				newFileDir.mkdir();
				if (newFileDir.exists()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 创建单个文件
	 *
	 * @Author StoneJxn
	 * @Date 2016年2月25日 上午9:15:58
	 * @param path
	 * @return
	 */
	public static boolean createFile(String path) {
		try {
			if (!TextUtils.isEmpty(path)) {
				File fileNew = new File(path);
				if (!fileNew.exists()) {
					fileNew.createNewFile();
					if (fileNew.exists()) {
						return true;
					} else {
						return false;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据给定的文件的完整路径，判断 并创建文件夹 及文件
	 *
	 * @author xnjiang
	 * @param filePath
	 * @return
	 * @since v0.0.1
	 */
	public static boolean createDirAndFile(String filePath) {
		try {
			File fileNew = new File(filePath);
			if (!fileNew.exists()) {
				if (!fileNew.getParentFile().exists()) {
					fileNew.getParentFile().mkdirs();
				}
				fileNew.createNewFile();
				if (fileNew.exists()) {
					return true;
				} else {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 检测文件是否存在
	 *
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		try {
			if (!TextUtils.isEmpty(path)) {
				File file = new File(path);
				return file.exists();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 * 检测文件是否存在
	 *
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(File file) {
		try {
			return file.exists();
		} catch (Exception ex) {
		}

		return false;
	}

	/**
	 * 比较两个文件是否相同
	 *
	 * @param file1
	 * @param file2
	 * @return true 相同,false 不同
	 */
	public static boolean isCompareFiles(File file1, File file2) {
		if (file1.getPath().equalsIgnoreCase(file2.getPath())) {
			return true;
		}
		return false;
	}

	/**
	 * 比较两个文件是否相同
	 *
	 * @param path1
	 * @param path2
	 * @return true 相同,false 不同
	 */
	public static boolean isCompareFiles(String path1, String path2) {
		if (path1.equalsIgnoreCase(path2)) {
			return true;
		} else {
			return isCompareFiles(new File(path1), new File(path2));
		}
	}

	/**
	 * 建立私有文件
	 *
	 * @param ctx
	 * @param fileName
	 *            私有文件夹下的路径如：database\aa.db3
	 * @return
	 * @throws IOException
	 */
	public static File creatSystemDataFile(Context ctx, String fileName) {
		try {
			File file = new File(getSystemFilesPath(ctx) + fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 建立私有目录
	 *
	 * @param ctx
	 * @param dirName
	 *            私有文件夹下的路径如：database\aa.db3
	 * @return
	 */
	public static File creatSystemDataDir(Context ctx, String dirName) {
		File dir = new File(getSystemFilesPath(ctx) + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 删除私有文件
	 *
	 * @param ctx
	 * @param fileName
	 * @return
	 */
	public static boolean deleteSystemDataFile(Context ctx, String fileName) {
		File file = new File(getSystemFilesPath(ctx) + fileName);
		return deleteFile(file);
	}

	/**
	 * 删除私有目录
	 *
	 * @param ctx
	 * @param dirName
	 * @return
	 */
	public static boolean deleteSystemDataDir(Context ctx, String dirName) {
		File file = new File(getSystemFilesPath(ctx) + dirName);
		return deleteDir(file);
	}

	/**
	 * 更改私有文件名
	 *
	 * @param ctx
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean renameSystemDataFile(Context ctx, String oldName, String newName) {
		File oldFile = new File(getSystemFilesPath(ctx) + oldName);
		File newFile = new File(getSystemFilesPath(ctx) + newName);
		return oldFile.renameTo(newFile);
	}

	/**
	 * 在私有目录下进行文件复制
	 *
	 * @param ctx
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean copySystemDataFileTo(Context ctx, String srcFileName, String destFileName) {
		try {
			File srcFile = new File(getSystemFilesPath(ctx) + srcFileName);
			File destFile = new File(getSystemFilesPath(ctx) + destFileName);
			return copyFileTo(srcFile, destFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 复制私有目录里指定目录的所有文件
	 *
	 * @param ctx
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean copySystemDataFilesTo(Context ctx, String srcDirName, String destDirName) {
		try {
			File srcDir = new File(getSystemFilesPath(ctx) + srcDirName);
			File destDir = new File(getSystemFilesPath(ctx) + destDirName);
			return copyFilesTo(srcDir, destDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 移动私有目录下的单个文件
	 *
	 * @param ctx
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveSystemDataFileTo(Context ctx, String srcFileName, String destFileName) {
		try {
			File srcFile = new File(getSystemFilesPath(ctx) + srcFileName);
			File destFile = new File(getSystemFilesPath(ctx) + destFileName);
			return moveFileTo(srcFile, destFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 移动私有目录下的指定目录下的所有文件
	 *
	 * @param ctx
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveSystemDataFilesTo(Context ctx, String srcDirName, String destDirName) {
		try {
			File srcDir = new File(getSystemFilesPath(ctx) + srcDirName);
			File destDir = new File(getSystemFilesPath(ctx) + destDirName);
			return moveFilesTo(srcDir, destDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将文件写入应用私有的files目录。如:writeFile("test.txt");
	 *
	 * @param ctx
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean writeSystemFile(Context ctx, String fileName, String content) {
		try {
			OutputStream os = ctx.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			os.write(content.getBytes());
			os.flush();
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 在原有文件上继续写文件。如:appendFile("test.txt");
	 *
	 * @param ctx
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean appendSystemFile(Context ctx, String fileName, String content) {
		try {
			OutputStream os = ctx.openFileOutput(fileName, Context.MODE_APPEND);
			os.write(content.getBytes());
			os.flush();
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从应用的私有目录files读取文件。如:readFile("test.txt");
	 *
	 * @param ctx
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readSystemFile(Context ctx, String fileName) {
		try {
			InputStream is = ctx.openFileInput(fileName);
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			while (is.read(bytes) != -1) {
				arrayOutputStream.write(bytes, 0, bytes.length);
			}
			is.close();
			arrayOutputStream.close();
			return new String(arrayOutputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 写数据到SD中的文件
	 *
	 * @param filePath
	 * @param write_str
	 */
	public static void writeFileSdcardFile(String filePath, String fileContent) {
		try {

			FileOutputStream fout = new FileOutputStream(filePath);
			byte[] bytes = fileContent.getBytes();

			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读数据从SD中的文件
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readFileSdcardFile(String filePath) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(filePath);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = new String(buffer, "UTF-8");

			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Android获取文件的HashCode
	 *
	 * @param path
	 */
	public static int getFileHashCode(File file) {
		if (file == null || !file.exists() || !file.isFile()) {
			return 0;
		}
		return file.hashCode();
	}

	/**
	 * Android获取文件的MD5校验码(主要用于判断文件的一致性)
	 *
	 * @Author StoneJxn
	 * @Date 2016年2月25日 上午9:10:46
	 * @param filePath
	 * @return
	 */
	public static String getFileMD5(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		}
		return getFileMD5(new File(filePath));
	}

	/**
	 * Android获取文件的MD5校验码(主要用于判断文件的一致性)
	 *
	 * @Author StoneJxn
	 * @Date 2016年2月25日 上午9:10:46
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {
		if (file == null || !file.isFile()) {
			return "";
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	/**
	 * 获取文件夹中文件的MD5值
	 *
	 * @param file
	 * @param listChild
	 *            true递归子目录中的文件
	 * @return
	 */
	public static Map<String, String> getDirMD5(String filePath, boolean listChild) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		return getDirMD5(new File(filePath), listChild);
	}

	/**
	 * 获取文件夹中文件的MD5值
	 *
	 * @param file
	 * @param listChild
	 *            true递归子目录中的文件
	 * @return
	 */
	public static Map<String, String> getDirMD5(File file, boolean listChild) {
		if (!file.isDirectory()) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(getDirMD5(f, listChild));
			} else {
				md5 = getFileMD5(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

	/**
	 * 获取文件的读写权限
	 *
	 * @param path
	 * @return (-1)-不能访问,0-只读,1-读写
	 */
	public static int getFilePermission(String path) {
		if (TextUtils.isEmpty(path)) {
			return -1;
		}
		return getFilePermission(new File(path));
	}

	/**
	 * 获取文件的读写权限
	 *
	 * @param path
	 * @return (-1)-不能访问,0-只读,1-读写
	 */
	public static int getFilePermission(File f) {
		int intPermission = -1;
		if (isFileExist(f)) {
			if (f.canRead()) {
				intPermission = 0;
			}
			if (f.canWrite()) {
				intPermission = 1;
			}
		}
		return intPermission;
	}

	/**
	 * 获取外置SD卡文件夹的读写权限
	 *
	 * @param path
	 * @return (-1)-不能访问,0-只读,1-读写,2-只写
	 */
	public static boolean getFilePermissionExternalSD(String path) {
		if (!TextUtils.isEmpty(path)) {
			if (getFilePermission(new File(path)) > 0 && new File(path).isDirectory()) {
				File strTempFile = new File(path + "/.abcdefghijklmnopqrstuwxyz_123456789");
				deleteDirOrFile(strTempFile);
				strTempFile.mkdir();
				if (isFileExist(strTempFile)) {
					deleteDirOrFile(strTempFile);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 获取内置SD卡路径
	 *
	 * @return
	 */
	public static String getSDCardPathInner() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取外置SD卡路径
	 *
	 * @return 应该就一条记录或空
	 */
	public static String getSDCardPathOuter() {
		List<String> lResult = new ArrayList<String>();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("extSdCard") || line.contains("media_rw")) {
					String[] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if (file.isDirectory()) {
						lResult.add(path);
					}
				}
			}
			isr.close();
		} catch (Exception e) {
		}
		return lResult.toString();
	}

	/**
	 * 获取当前文件的上一级文件路径
	 *
	 * @param file
	 * @return
	 */
	public static String getFilePathOfParent(File file) {
		if (file == null) {
			return "";
		}
		return file.getParentFile().getPath() + File.separator;
	}

	/**
	 * 获取当前文件的上一级文件路径
	 *
	 * @param path
	 * @return
	 */
	public static String getFilePathOfParent(String path) {
		if (!TextUtils.isEmpty(path)) {
			return getFilePathOfParent(new File(path));
		} else {
			return "";
		}
	}

	/*** 获取文件个数 ***/
	public static int getFileCount(String path) {// 递归求取目录文件个数
		if (TextUtils.isEmpty(path)) {
			return 0;
		}
		return getFileCount(new File(path));
	}

	/*** 获取文件个数 ***/
	public static int getFileCount(File f) {// 递归求取目录文件个数
		int count = 0;
		if (f.isDirectory()) {
			File flist[] = f.listFiles();
			count = flist.length;
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					count = count + getFileCount(flist[i]);
					count--;
				}
			}
		} else {
			count = 1;
		}
		return count;
	}

	/**
	 * 获取文件大小 (单位：b)
	 *
	 * @param f
	 * @param boolFolderCount是否统计文件夹大小
	 *            （文件夹统计比较耗时）
	 * @return 文件默认返回 0
	 */
	public static long getFileSize(String path, boolean boolFolderCount) {
		return getFileSize(new File(path), boolFolderCount);
	}

	/**
	 * 获取文件大小 (单位：b)
	 *
	 * @param f
	 * @param boolFolderCount是否统计文件夹大小
	 *            （文件夹统计比较耗时）
	 * @return 文件默认返回 0
	 */
	public static long getFileSize(File f, boolean boolFolderCount) {
		long size = 0;
		try {
			if (f.isFile()) {// 文件处理
				if (f.exists()) {// 存在
					size = f.length();
				}
			} else {// 文件夹处理
				if (boolFolderCount) {
					File flist[] = f.listFiles();
					if (flist != null && flist.length > 0) {
						for (File item : flist) {
							if (item.isDirectory()) {
								size = size + getFileSize(item, boolFolderCount);
							} else {
								size = size + item.length();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/*** 就是转化文件大小，KB M G等之间。 ***/
	public static String formatFileSize(Context context, long sizeBytes) {
		return Formatter.formatFileSize(context, sizeBytes);
	}

	/*** 转换文件大小单位(B/KB/MB/GB) ***/
	public static String formatFileSize(long fileS) {// 转换文件大小

		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < (1024 * 1024)) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < (1024 * 1024 * 1024)) {
			fileSizeString = df.format((double) fileS / (1024 * 1024)) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / (1024 * 1024 * 1024)) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 获取Phone容量信息(单位：Bytes)
	 *
	 * @return
	 */
	public static String getPhoneCapacity() {
		// 获取本机信息
		File data = Environment.getDataDirectory();
		StatFs statFs = new StatFs(data.getPath());
		int availableBlocks = statFs.getAvailableBlocks();// 可用存储块的数量
		int blockCount = statFs.getBlockCount();// 总存储块的数量

		int size = statFs.getBlockSize();// 每块存储块的大小

		int totalSize = blockCount * size;// 总存储量

		int availableSize = availableBlocks * size;// 可用容量

		String phoneCapacity = formatFileSize(availableSize) + "/" + formatFileSize(totalSize);

		return phoneCapacity;
	}

	/**
	 * 获取SDCard容量信息(单位：Bytes)
	 *
	 * @return
	 */
	public static String getSDCardCapacity() {
		// 获取sdcard信息
		File sdData = Environment.getExternalStorageDirectory();
		StatFs sdStatFs = new StatFs(sdData.getPath());

		int sdAvailableBlocks = sdStatFs.getAvailableBlocks();// 可用存储块的数量
		int sdBlockcount = sdStatFs.getBlockCount();// 总存储块的数量
		int sdSize = sdStatFs.getBlockSize();// 每块存储块的大小
		int sdTotalSize = sdBlockcount * sdSize;
		int sdAvailableSize = sdAvailableBlocks * sdSize;

		String sdcardCapacity = formatFileSize(sdAvailableSize) + "/" + formatFileSize(sdTotalSize);
		return sdcardCapacity;
	}

	/**
	 * 获得手机内置sd卡总大小(三星 4.0之后的 /storage/sdcard0) 或者获得手机外置sd卡SD卡总大小(如三星 4.0之后的
	 * /storage/extSdCard)
	 *
	 * 注意：4.0之前手机内置sd卡不包含手机内存，只含标准sd容量；4.0之后怎么包含这两个
	 *
	 * @param pPath
	 *            如：/storage/sdcard0，/storage/extSdCard
	 * @return
	 */
	public static String getInOrOutSDTotalSize(Context pContext, String pPath) {
		return getTotalSize(pContext, pPath);
	}

	/**
	 * 获得手机内置sd卡剩余容量，即可用大小 或者获得手机外置sd卡剩余容量，即可用大小
	 *
	 * @param pPath
	 *            如：/storage/sdcard0，/storage/extSdCard
	 * @return
	 */
	public static String getInOrOutSDAvailableSize(Context pContext, String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		return getAvailableSize(pContext, path);
	}

	/**
	 * 获得机身内存总大小(/data)
	 *
	 * @return
	 */
	public static String getRomTotalSize(Context pContext) {
		File path = Environment.getDataDirectory();
		String mPath = path.getPath();
		return getTotalSize(pContext, mPath);
	}

	/**
	 * 获得机身剩余内存
	 *
	 * @return
	 */
	public static String getRomAvailableSize(Context pContext) {
		File path = Environment.getDataDirectory();
		String mPath = path.getPath();
		return getAvailableSize(pContext, mPath);
	}

	/**
	 * 比较sd容量是否够需要
	 *
	 * @param phoneCapacity
	 *            盘符的大小
	 * @return true 够用；false 不够用
	 */
	public static boolean compareSize(String phoneCapacity) {
		String unit = phoneCapacity.substring(phoneCapacity.length() - 2).trim();
		String size = phoneCapacity.substring(0, phoneCapacity.length() - 2).trim();
		if ("GB".equalsIgnoreCase(unit)) {
			return true;
		} else if ("MB".equalsIgnoreCase(unit)) {
			int mSize = Math.round(Float.parseFloat(size));
			if (size != null && mSize > 100) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private static String getTotalSize(Context pContext, String mPath) {
		StatFs stat = new StatFs(mPath);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(pContext, blockSize * totalBlocks);
	}

	private static String getAvailableSize(Context pContext, String mPath) {
		StatFs stat = new StatFs(mPath);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(pContext, blockSize * availableBlocks);
	}

	// TODO：应用程序Asset文件的操作
	/**
	 * 读取Assets目录下的文件内容到List<String>
	 *
	 * @param context
	 * @return
	 */
	public static List<String> readAssetsFile2List(Context context, String assetsFileName) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open(assetsFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * InputStream.available()得到字节数，然后一次读取完。
	 *
	 * @author xnjiang
	 * @param context
	 * @param assetName
	 * @since v0.0.1
	 */
	public static String readAssetsFile2String1(Context context, String assetsFileName) {
		String content = "";
		try {
			InputStream is = context.getAssets().open(assetsFileName);
			if (is != null) {
				DataInputStream dIs = new DataInputStream(is);
				int length = dIs.available();
				byte[] buffer = new byte[length];
				dIs.read(buffer);
				content = new String(buffer, "UTF-8");
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * BufferedReader.readLine()行读取再加换行符，最后用StringBuilder.append()连接成字符串。
	 *
	 * @author xnjiang
	 * @param context
	 * @param assetName
	 * @return
	 * @since v0.0.1
	 */
	public static String readAssetsFile2String2(Context context, String assetsFileName) {
		StringBuilder sb = new StringBuilder("");
		String content = "";
		try {
			InputStream is = context.getAssets().open(assetsFileName);
			if (is != null) {
				BufferedReader d = new BufferedReader(new InputStreamReader(is));
				while (d.ready()) {
					sb.append(d.readLine() + "\n");
				}
				content = new String(sb.toString().getBytes(), "UTF-8");
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * InputStreamReader先指定以UTF8读取文件，再进行读取读取操作：
	 *
	 * @author xnjiang
	 * @param context
	 * @param assetName
	 * @return
	 * @since v0.0.1
	 */
	public static String readAssetsFile2String3(Context context, String assetName) {
		StringBuilder sb = new StringBuilder("");
		String content = "";
		try {
			InputStream is = context.getAssets().open(assetName);
			if (is != null) {
				BufferedReader d = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while (d.ready()) {
					sb.append(d.readLine() + "\n");
				}
				content = sb.toString();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 复制Assets目录下的单个文件到指定文件夹
	 *
	 * @param ctx
	 * @param fromFile
	 * @param newDir
	 * @return
	 */
	public static boolean copyAssetsFile2Dir(Context ctx, String assetsFile, String newDir) {
		try {
			// 在样例文件夹下创建文件
			File fileTarget = new File(newDir, assetsFile);
			// 如果文件已经存在则跳出
			if (fileTarget.exists()) {
				return true;
			}

			InputStream input = ctx.getAssets().open(assetsFile);
			OutputStream output = new FileOutputStream(fileTarget);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			input.close();
			output.close();
			buffer = null;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 复制Assets目录下的单个文件到指定文件夹
	 *
	 * @param ctx
	 * @param assetsDir
	 * @param dir
	 */
	public static void copyAssetsDir2Dir(Context ctx, String assetsDir, String newDir) {
		String[] files;
		try {
			files = ctx.getResources().getAssets().list(assetsDir);
		} catch (IOException e1) {
			return;
		}
		File fileTarget = new File(newDir);
		// if this directory does not exists, make one.
		if (!fileTarget.exists()) {
			if (!fileTarget.mkdirs()) {
				Log.e("--CopyAssets--", "cannot create directory.");
			}
		}
		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				if (fileName.compareTo("images") == 0 || fileName.compareTo("sounds") == 0 || fileName.compareTo("webkit") == 0) {
					continue;
				}
				// 判断是否为文件夹，通过是否含有“.”来区分文件夹
				if (!fileName.contains(".")) {
					if (0 == assetsDir.length()) {
						copyAssetsDir2Dir(ctx, fileName, newDir + fileName + "/");
					} else {
						copyAssetsDir2Dir(ctx, assetsDir + "/" + fileName, newDir + fileName + "/");
					}
					continue;
				}

				File outFile = new File(fileTarget, fileName);
				// 判断文件是否存在，存在跳过，不存在就复制
				if (outFile.exists()) {
					continue; // outFile.delete();
				}
				InputStream input = null;
				if (0 != assetsDir.length())
					input = ctx.getAssets().open(assetsDir + "/" + fileName);
				else
					input = ctx.getAssets().open(fileName);
				OutputStream output = new FileOutputStream(outFile);
				// Transfer bytes from in to out
				byte[] buffer = new byte[1024];
				int length;
				while ((length = input.read(buffer)) > 0) {
					output.write(buffer, 0, length);
				}
				input.close();
				output.close();
				buffer = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * assets文件夹资源的访问
	 *
	 * assets文件夹里面的文件都是保持原始的文件格式，需要用AssetManager以字节流的形式读取文件。
	 *
	 * 1. 先在Activity里面调用getAssets()来获取AssetManager引用。
	 *
	 * 2. 再用AssetManager的open(String fileName, int
	 * accessMode)方法则指定读取的文件以及访问模式就能得到输入流InputStream。
	 *
	 * 3. 然后就是用已经open file 的inputStream读取文件，读取完成后记得inputStream.close()。
	 *
	 * 4.调用AssetManager.close()关闭AssetManager。 需要注意的是，来自Resources和Assets
	 * 中的文件只可以读取而不能进行写的操作
	 */

	/**
	 * * 通过资源文件名称、获取源文件ID
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月9日 上午10:24:37
	 * @param context
	 * @param resFolder
	 *            资源文件夹 drawable、raw、string
	 * @param fileName
	 * @return
	 */
	public static int getResIDFromResName(Context context, String resFolder, String fileName) {
		/** 通过资源名称找到资源图片 */
		int resID = -1;
		try {
			fileName = fileName.replace(".png", "");
			fileName = fileName.replace(".jpg", "");
			fileName = fileName.replace(".jpeg", "");
			fileName = fileName.replace(".gif", "");
			if (resFolder.trim().length() > 0) {
				resID = context.getResources().getIdentifier(fileName, resFolder, context.getPackageName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resID;
	}

	/**
	 * 以下为从Raw文件中读取：
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月9日 上午10:24:00
	 * @param context
	 * @param resId
	 * @return
	 */
	public static InputStream getStreamFromRawResID(Context context, int resId) {
		try {
			InputStream stream = context.getResources().openRawResource(resId);
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以下为从Raw文件中读取
	 *
	 * @param context
	 * @param resFolder
	 *            资源文件夹 drawable、raw
	 * @param fileName
	 *            资源文件名
	 * @return
	 */
	public static InputStream getStreamFromResFileName(Context context, String resFolder, String fileName) {
		try {
			InputStream stream = context.getResources().openRawResource(getResIDFromResName(context, resFolder, fileName));
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以下为直接从assets读取
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月9日 上午10:23:34
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static InputStream getStreamFromAssetsFileName(Context context, String fileName) {
		try {
			InputStream stream = context.getResources().getAssets().open(fileName);
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取Uri格式 的本地资源文件
	 *
	 * @param context
	 * @param resID
	 *            资源文件号 ：R.raw.sample
	 * @param resPath
	 *            资源文件名: raw/sample.png、drawable/sample.png、assets/sample.png
	 * @return
	 */
	public static Uri getUriFromResource(Context context, int resID, String resPath) {
		try {
			/*
			 * Android把res/raw的资源转化为Uri形式访问(android.resource://)
			 * Andorid应用会在打包成Apk时把应用中使用的资源文件都打包进去了
			 * ，尤其是我们熟悉的assets和res文件夹里面存放的资源文件，
			 * 一般情况下我们可以直接使用AssetManager类访问Apk下的assets目录
			 * ，而对于res目录下的资源，我们很少直接使用他们，基本上都是通过它们的id在代码中使用。
			 * 那么是否可以直接访问APK压缩包中Res目录下的内容呢? 比如需要访问res/raw这样的文件夹?
			 * 如果我们想访问res/raw/sample.png文件，可以使用android.resource://package_name/"
			 * +
			 * R.raw.sample.png这种格式来获取对应的Uri（其中package_name是应用的包名），有了这个Uri那么一切都好办了
			 * 。 完整的处理代码为 Uri uri =
			 * Uri.parse("android.resource://package_name/raw/sample.png");
			 * 这样就可以通过Uri来使用apk中res/raw目录下的文件了
			 */
			if (resID > 0) {
				return Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + resID);
			} else {
				return Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + resPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过string资源中的name获取vlaue
	 *
	 * @param context
	 * @param resName
	 *            资源文件名
	 * @return
	 */
	public static String getStringFromResName1(Context context, String resName) {
		try {
			return context.getResources().getString(getResIDFromResName(context, "string", resName));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "getStringFromResName is Error! ErrorCode = " + e.getMessage());
		}
		return "";
	}

	/**
	 * Java中用到文件操作时,经常要验证文件名是否合法. 用File类的createNewFile()方法的确很管用.
	 * 但当要批量验证时,效率上就会有问题.正则匹配的开销比创建文件少了很多. 那么一个合法的文件(Win下)应该符合如下规则:
	 * 1.文件名不能为空,空在这里有两个意思: 文件名(包括扩展名)长度为0或仅由空字符组成(包括\t\b等不可见的转义字符)
	 * 文件名和扩展名不能同时为空.但实际上我们可以用程序创建出类似.project,..txt等形式的文件,但却创建不出类似abc.的文件
	 * 2.文件名中不能包含\/:*?"<>|中的任意字符 3.文件名(包括扩展名)的长度不得大于255个字符
	 * 事实上形如".."(不包含引号,下同)的文件也不能被创建. 不合法的文件还有类似" aa", "aa ",
	 * "aa."(会被创建为"aa",也把它算作不合法),"a\ta"(\t为制表符等不可见字符(除空格外))
	 * 于是我们得到了文件名命名规则的更详细规定: 1.首尾不能有空字符(空格、制表符、换页符等空白字符的其中任意一个),文件名尾不能为.号
	 * 2.文件名和扩展名不能同时为空 3.文件名中不能包含\/:*?"<>|中的任意字符 4.文件名(包括扩展名)的长度不得大于255个字符
	 * 5.在1.的条件下,文件名中不能出出现除空格符外的任意空字符. 出现控制字符其实也算不合法，但因为情况太复杂，就不做判断了。于是有如下匹配
	 * 首字符: [^\s\\/:\*\?\"<>\|] 尾字符: [^\s\\/:\*\?\"<>\|\.] 其它字符:
	 * (\x20|[^\s\\/:\*\?\"<>\|])* \s 只能匹配下面六种字符（via:java.util.regex.Pattern）：
	 * 半角空格（ ） 水平制表符（\t） 竖直制表符 回车（\r） 换行（\n） 换页符（\f）
	 */
	/**
	 * 检查文件名是否合法 不能包含
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月9日 上午11:16:32
	 * @param fileName
	 * @return
	 */
	public static boolean isFileNameValid(String strFileName) {
		try {
			if (TextUtils.isEmpty(strFileName) || strFileName.length() > 255) {
				return false;
			} else {
				return strFileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 过滤文件名中的非法字符
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月9日 上午11:23:24
	 * @param str
	 * @return
	 */
	public static String getFileNameFilter(String strFileName) {
		try {
			if (!TextUtils.isEmpty(strFileName)) {
				String regEx = "[/\\:*?<>|\"\n\t]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(strFileName);
				return m.replaceAll("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 字符串过滤：只允许输入数字、字符、汉字
	 *
	 * @Author StoneJxn
	 * @Date 2016年3月28日 下午4:41:00
	 * @param strInput
	 * @return
	 */
	public static String getStringFilter(String strInput) {
		try {
			if (!TextUtils.isEmpty(strInput)) {
				// 只允许字母、数字和汉字
				String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(strInput);
				return m.replaceAll("").trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过路径获取文件对象
	 *
	 * @param file
	 * @return
	 */
	public static File getFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		return new File(filePath);
	}

	/**
	 * 获取文件名
	 *
	 * @param file
	 * @return
	 */
	public static String getFileName(File file) {
		if (file == null) {
			return "";
		}
		return file.getName();
	}

	/**
	 * 获取文件名
	 *
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		return getFileName(new File(path));
	}

	/**
	 * 获取不带扩展名的文件名
	 *
	 * @param file
	 * @return
	 */
	public static String getFileNameNoExtension(File file) {
		if (file == null) {
			return "";
		}
		String filename = file.getName();
		if (file.isFile() && !TextUtils.isEmpty(filename)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * 获取不带扩展名的文件名
	 *
	 * @param path
	 * @return
	 */
	public static String getFileNameNoExtension(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		return getFileNameNoExtension(new File(path));
	}

	/**
	 * 获取文件扩展名(包含前面那个点 ‘.’)
	 *
	 * @param path
	 * @return
	 */
	public static String getFileExtensionPoint(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		return getFileExtensionPoint(new File(path));
	}

	/**
	 * 获取文件扩展名(包含前面那个点 ‘.’)
	 *
	 * @param file
	 * @return
	 */
	public static String getFileExtensionPoint(File file) {
		if (file == null || file.isDirectory()) {
			return "";
		}
		String filename = file.getName();
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot);
			}
		}
		return "";
	}

	/**
	 * 获取文件扩展名(不包含前面那个点 ‘.’)
	 *
	 * @param path
	 * @return
	 */
	public static String getFileExtensionNoPoint(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		return getFileExtensionNoPoint(new File(path));
	}

	/**
	 * 获取文件扩展名(不包含前面那个点 ‘.’)
	 *
	 * @param file
	 * @return
	 */
	public static String getFileExtensionNoPoint(File file) {
		if (file == null || file.isDirectory()) {
			return "";
		}
		String filename = file.getName();
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	/**
	 * <b>通过文件file对象获取文件标识MimeType</b><br>
	 * <br>
	 * MIME type的缩写为(Multipurpose Internet Mail Extensions)代表互联网媒体类型(Internet
	 * media type)，
	 * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
	 * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
	 *
	 * @param file
	 * @return
	 */
	public static String getFileMimeTypeFromFile(File file) {
		String extension = getFileExtensionNoPoint(file);
		return getFileMimeTypeFromExtension(extension);
	}

	/**
	 * <b>通过文件的扩展名Extension获取文件标识MimeType</b><br>
	 * <br>
	 * MIME type的缩写为(Multipurpose Internet Mail Extensions)代表互联网媒体类型(Internet
	 * media type)，
	 * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
	 * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
	 *
	 * @param extension
	 * @return
	 */
	public static String getFileMimeTypeFromExtension(String extension) {
		try {
			if (TextUtils.isEmpty(extension)) {
				return "*/*";
			}
			extension = extension.replace(".", "");
			if (extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("wps")) {
				extension = "doc";
			} else if (extension.equalsIgnoreCase("xlsx")) {
				extension = "xls";
			} else if (extension.equalsIgnoreCase("pptx")) {
				extension = "ppt";
			}
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			if (mimeTypeMap.hasExtension(extension)) {
				// 获得txt文件类型的MimeType
				return mimeTypeMap.getMimeTypeFromExtension(extension);
			} else {
				if (extension.equalsIgnoreCase("dwg")) {
					return "application/x-autocad";
				} else if (extension.equalsIgnoreCase("dxf")) {
					return "application/x-autocad";
				} else if (extension.equalsIgnoreCase("ocf")) {
					return "application/x-autocad";
				} else {
					return "*/*";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "*/*";
	}

	/**
	 * <b>通过文件标识MimeType获取文件的扩展名Extension</b><br>
	 * <br>
	 * MIME type的缩写为(Multipurpose Internet Mail Extensions)代表互联网媒体类型(Internet
	 * media type)，
	 * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
	 * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
	 *
	 * @param mimetype
	 * @return
	 */
	public static String getFileExtensionFromMimeType(String mimeType) {
		try {
			if (TextUtils.isEmpty(mimeType)) {
				return "";
			}
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			if (mimeTypeMap.hasMimeType(mimeType)) {
				// 获得"text/html"类型所对应的文件类型如.txt、.jpeg
				return mimeTypeMap.getExtensionFromMimeType(mimeType);
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// TODO:Gmail附件图纸处理
	/**
	 * 获取gmail附件的名称和大小
	 *
	 * @param context
	 * @param documentUri
	 * @return
	 */
	public static String getAttachmetName(Context context, Uri documentUri) {
		if (null != documentUri) {
			final String uriString = documentUri.toString();
			String documentFilename = null;

			final int mailIndexPos = uriString.lastIndexOf("/attachments");
			if (mailIndexPos != -1) {
				final Uri curi = documentUri;
				final String[] projection = new String[] { OpenableColumns.DISPLAY_NAME };
				final Cursor cursor = context.getContentResolver().query(curi, projection, null, null, null);
				if (cursor != null) {
					final int attIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (attIdx != -1) {
						cursor.moveToFirst();
						documentFilename = cursor.getString(attIdx);
					}
					cursor.close();
				}
			}
			return documentFilename;
		}
		return null;
	}

	/**
	 * 获取Gmail附件的路径
	 *
	 * @param uri
	 * @return
	 */
	public static String getGamiFilePath(Context context, Uri uri) {
		String strFileName = getAttachmetName(context, uri);
		String strAbsolutePath = getAvailableFilesPathSystem(context) + "/" + strFileName;
		try {
			InputStream attachment = context.getContentResolver().openInputStream(uri);
			if (attachment == null)
				Log.e("onCreate", "cannot access mail attachment");
			else {
				FileOutputStream tmp = new FileOutputStream(strAbsolutePath);
				byte[] buffer = new byte[1024];
				while (attachment.read(buffer) > 0)
					tmp.write(buffer);

				tmp.close();
				attachment.close();
			}
		} catch (FileNotFoundException e) {
			strAbsolutePath = "";
			e.printStackTrace();
		} catch (IOException e) {
			strAbsolutePath = "";
			e.printStackTrace();
		}
		return strAbsolutePath;
	}

	/**
	 * 获取文件的绝对路径，相应地可以改成其他多媒体类型如audio等等
	 *
	 * @param context
	 *            必须是Activity的实例
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getAbsoluteImagePath(Context context, Uri uri) {
		String strAbsolutePath = "";
		String[] proj = { MediaColumns.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		strAbsolutePath = cursor.getString(column_index);
		cursor.close();
		return strAbsolutePath;
	}

	public static boolean checkAndMakeDir(String fileDir) {
		File file = new File(fileDir);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean isJpgFile(String fileName) {

		if (fileName != null) {
			if (fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
				return true;
			}

			int pointIndex = fileName.lastIndexOf('.');
			if (pointIndex != -1) {
				String suffix = fileName.substring(pointIndex, fileName.length());
				if (suffix.equalsIgnoreCase(".jpg")) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean fileRename(String oldPath, String newPath) {
		File file = new File(oldPath);
		if (!file.exists()) {
			return false;
		} else {
			file.renameTo(new File(newPath));
			return true;
		}
	}

	// TODO:XML文件读写操作代码
	/**
	 * 创建XML字符串
	 */
	public void writeXMLString() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);

			// 文档开始，标题行
			serializer.startDocument("UTF-8", null);
			// 第一层
			serializer.startTag("", "paralist");
			// 第二层数据层
			// 第一个数据
			serializer.startTag("", "para");
			serializer.startTag("", "value");
			serializer.text("Stonejxn");
			serializer.endTag("", "value");
			serializer.startTag("", "name");
			serializer.text("account");
			serializer.endTag("", "name");
			serializer.endTag("", "para");
			// 第二个数据
			serializer.startTag("", "para");
			serializer.startTag("", "value");
			serializer.text("a123456789");
			serializer.endTag("", "value");
			serializer.startTag("", "name");
			serializer.text("password");
			serializer.endTag("", "name");
			serializer.endTag("", "para");

			serializer.endTag("", "paralist");
			serializer.endDocument();
			debugTool.d("XML", writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * XmlPullParser 方式解析XML文件
	 *
	 * @author xnjiang
	 * @param inStream
	 * @return
	 * @since v0.0.1
	 */
	public static void readXMLFromPull(InputStream inStream) {

		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					// 文档开始部分;
					break;
				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (name.equalsIgnoreCase("person")) {
						int id = new Integer(parser.getAttributeValue(null, "id"));
						String sex = parser.getAttributeValue(null, "sex");
					}
					break;
				case XmlPullParser.END_TAG:// 结束元素事件
					break;
				}
				eventType = parser.next();
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将XML字符串写入XML文件
	 */
	public void writeXMLStringToXMLFile(Context ctx, String strFilePath, String strFileContent) {
		try {
			writeSystemFile(ctx, strFilePath, strFileContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取表情配置文件
	 *
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/********************* 取得文件夹下的音乐路径 传入目录名称、目录路径 ************************************/
	// TODO 取得文件夹下的音乐路径 传入目录名称、目录路径
	/**
	 *
	 * 取得文件夹下的音乐路径 传入目录名称、目录路径
	 *
	 * @author xnjiang
	 * @param context
	 * @param rootPath
	 * @return
	 * @since v0.0.1
	 */
	public static List<Map<String, Object>> getAudiosFromFolder(Context context, String rootPath) {
		List<Map<String, Object>> listPhotoInfo = new ArrayList<Map<String, Object>>();
		Map<String, Object> mItem = null;
		try {
			// 获取系通图片管理的数据库信息
			ContentResolver mContentResolver = context.getContentResolver();

			String[] projection = { BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST, MediaColumns.DATA, AudioColumns.DURATION, MediaColumns.SIZE };

			// String strSelection = Media._ID +
			// " in (select image_id from thumbnails) "
			String strSelection = BaseColumns._ID + "!='' ";
			if (!TextUtils.isEmpty(rootPath)) {
				strSelection += " and " + MediaColumns.DATA + " like '" + rootPath + "%' ";
			}
			Cursor cur = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, strSelection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (cur.moveToFirst()) {
				do {
					// 歌曲ID：MediaStore.Audio.Media._ID
					int audio_id = cur.getInt(cur.getColumnIndexOrThrow(BaseColumns._ID));

					// 歌曲的名称 ：MediaStore.Audio.Media.TITLE
					String audio_tilte = cur.getString(cur.getColumnIndexOrThrow(MediaColumns.TITLE));

					// 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
					String audio_album = cur.getString(cur.getColumnIndexOrThrow(AudioColumns.ALBUM));

					// 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
					String audio_artist = cur.getString(cur.getColumnIndexOrThrow(AudioColumns.ARTIST));

					// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
					String audio_url = cur.getString(cur.getColumnIndexOrThrow(MediaColumns.DATA));

					// 歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
					int audio_duration = cur.getInt(cur.getColumnIndexOrThrow(AudioColumns.DURATION));

					// 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
					long audio_size = cur.getLong(cur.getColumnIndexOrThrow(MediaColumns.SIZE));

					if (!audio_url.equals("")) {
						mItem = new HashMap<String, Object>();
						mItem.put("audio_id", audio_id);
						mItem.put("audio_tilte", audio_tilte);
						mItem.put("audio_album", audio_album);
						mItem.put("audio_artist", audio_artist);
						mItem.put("audio_url", audio_url);
						mItem.put("audio_duration", audio_duration);
						mItem.put("audio_size", audio_size);
						listPhotoInfo.add(mItem);
						// Log.i(TAG, "getAudiosFromFolder !rootPath = " +
						// rootPath + ",photo_path = " + photo_path);
					} else {
						continue;
					}
				} while (cur.moveToNext());
				Log.i(TAG, "getAudiosFromFolder 扫描结束!AudiosCount = " + listPhotoInfo.size());
			}
			if (cur != null) {
				cur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listPhotoInfo;
	}

	/********************* 获取手机通讯录联系人信息 ************************************/
	// TODO: 获取手机通讯录联系人信息
	/**
	 * 获取手机通讯录联系人信息(参数 strContactsName,strContactsNumber同时为""或null查询全部通讯录)
	 *
	 * @param strContactsName
	 *            联系人姓名(模糊查询,默认全部)
	 * @param strContactsNumber
	 *            联系人电话(模糊查询,默认全部)
	 * @return 返回一个List数组包含的属性<br>
	 *         <b>contactsName</b> 联系人姓名<br>
	 *         <b>contactsNamePY</b> 联系人姓名 (拼音简码)<br>
	 *         <b>contactsNumber</b> 手机号<br>
	 *         <b>contactsEmail</b> 电子邮箱<br>
	 *         <b>contactsSelected</b> 选择状态(<b>true</b>选中，<b>false</b>未选中)<br>
	 */
	public static List<Map<String, Object>> getContactsList(Context context, String strContactsName, String strContactsNumber) {
		List<Map<String, Object>> mListContacters = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		// 获取库Phone表字段(联系人显示名称 、手机号码、联系人的ID)
		String[] projection = null;// {Phone.CONTACT_ID,
		// Phone.DISPLAY_NAME,Phone.NUMBER};
		// 设置查询条件
		String strSelection = null;// " LENGTH(TRIM(" + Phone.DISPLAY_NAME
		// +"))>1 ";
		// 设置排序方式，排序字段可以设置多个
		String strOrderBy = null;// " sort_key_alt, " + Phone.DISPLAY_NAME ;
		// // 添加查询条件联系人姓名和联系人电话
		// if (!strContactsName.equalsIgnoreCase("")) {
		// strSelection += Phone.DISPLAY_NAME + " LIKE " + "'%" +
		// strContactsName + "%'";
		// }
		// if (!strContactsNumber.equalsIgnoreCase("")) {
		// strSelection = Phone.NUMBER + " LIKE " + "'" + strContactsNumber +
		// "%'";
		// }

		ContentResolver cr = context.getContentResolver();
		Cursor c_name = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, strSelection, null, strOrderBy);
		while (c_name.moveToNext()) {
			// 得到联系人ID
			String contactsID = c_name.getString(c_name.getColumnIndex(BaseColumns._ID));
			// 得到联系人名称
			String contactsName = c_name.getString(c_name.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			// 得到手机号码
			String contactsNumber = "";
			// 得到联系人email
			String contactsEmail = "";

			// 获取与联系人ID相同的手机号码,可能不止一个
			contactsNumber = "";
			Cursor c_number = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactsID, null, null);
			while (c_number.moveToNext()) {
				String number = c_number.getString(c_number.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
				number = number.replace("-", "");
				if (number != null && !number.trim().equalsIgnoreCase(""))
					contactsNumber = number;
			}
			c_number.close();

			// 获取与联系人ID相同的电子邮件,可能不止一个
			contactsEmail = "";
			Cursor c_email = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactsID, null, null);
			while (c_email.moveToNext()) {
				String email = c_email.getString(c_email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				if (email != null && !email.trim().equalsIgnoreCase(""))
					contactsEmail = email;
			}
			c_email.close();

			// if (StoneFunctions.isEmail(contactsEmail) ||
			// StoneFunctions.isMobileNumber(contactsNumber)) {
			mHashMap = new HashMap<String, Object>();
			mHashMap.put("contactsNamePY", "");
			mHashMap.put("contactsName", contactsName);
			mHashMap.put("contactsNumber", contactsNumber);
			mHashMap.put("contactsEmail", contactsEmail);
			mHashMap.put("contactsSelected", false);
			mListContacters.add(mHashMap);
			Log.i(TAG, "contactsName = " + contactsName + "contactsNumber = " + contactsNumber + "contactsEmail = " + contactsEmail);
			// }
		}
		c_name.close();
		return mListContacters;
	}

	/**
	 *
	 * 读取给定文件中的内容信息并返回
	 *
	 * @author xnjiang
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @since v0.0.1
	 */
	public static String readFileValue(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
				FileInputStream inputStream = new FileInputStream(fileName);
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
				outputStream.close();
				inputStream.close();
				byte[] data = outputStream.toByteArray();
				return new String(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "readFileValue is Error! ErrorCode = " + e.getMessage());
		}
		return "";
	}

	/** 删除SD卡中给定位置的文件 */
	public static Boolean DeleteFileFromSD(String strURL) {
		if (strURL == null || "".equals(strURL)) {
			return true;
		}
		// /**SD卡目录获取操作*/
		// //判断SD卡是否插入
		// Result=Environment.getExternalStorageState().equalsIgnoreCase(android.os.Environment.MEDIA_MOUNTED);
		// //获得SD卡根目录：
		// File sdFileRoot = Environment.getExternalStorageDirectory();
		// //获得私有根目录(程序根目录)：
		// String fileRoot = SQLiteContext.getFilesDir()+"\\";
		File myFile = new File(strURL);
		boolean Result = false;
		if (FileUtils.isSDExist()) {
			// 删除文件夹
			Result = myFile.delete();
		}

		// /**文件夹或文件夹操作：*/
		// //建立文件或文件夹
		// if (myFile.isDirectory())//判断是文件或文件夹
		// {
		// Result=myFile.mkdir(); //建立文件夹
		// //获得文件夹的名称：
		// String FileName = myFile.getName();
		// //列出文件夹下的所有文件和文件夹名
		// File[] files = myFile.listFiles();
		// //获得文件夹的父目录
		// String parentPath = myFile.getParent();
		// //修改文件夹名字
		// File myFileNew=new File(parentPath+FileName);
		// Result=myFile.renameTo(myFileNew);
		// //删除文件夹
		// Result=myFile.delete();
		// }
		// else
		// {
		// if (!myFile.exists()) {
		// try {
		// Result=myFile.createNewFile();//建立文件
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// //获得文件或文件夹的名称：
		// String FileName = myFile.getName();
		// //获得文件的父目录
		// String parentPath = myFile.getParent();
		// //修改文件名字
		// File myFileNew=new File(parentPath+FileName);
		// Result=myFile.renameTo(myFileNew);
		// //删除文件夹
		// Result=myFile.delete();
		// }
		return Result;
	}

	/**
	 * 判断SD卡中给定位置的文件是否存在
	 *
	 * @param strURL
	 * @return true 存在 false 不存在
	 */
	public static Boolean checkFileExists(String strURL) {
		if (strURL == null || "".equals(strURL)) {
			return false;
		}
		File myFile = new File(strURL);
		boolean Result = true;
		if (FileUtils.isSDExist()) {
			Result = myFile.exists(); // 判断文件是否存在
		}
		return Result;
	}

	/**
	 * 将Double型数据的小数做保留处理
	 *
	 * @param dblValue
	 *            输入数值
	 * @param intPoint
	 *            保留位数
	 * @return
	 */
	public static double getDoublePoint(double dblValue, int intPoint) {
		try {
			double returnDouble;
			double parm = Math.pow(10, intPoint);
			returnDouble = ((int) (dblValue * parm)) / parm;
			return returnDouble;
		} catch (Exception e) {
			return dblValue;
		}
	}

	/**
	 * 将Double型数据的小数做保留处理
	 *
	 * @param v
	 *            输入数值
	 * @param scale
	 *            保留位数
	 * @return
	 */
	public static double getDoubleRound(Double v, int scale) {
		try {
			BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			return v;
		}
	}

	/**
	 * 将Double型数据的小数做保留处理后转成字符串
	 *
	 * @param v
	 *            输入数值
	 * @param scale
	 *            保留位数
	 * @return
	 */
	public static String getDouble2Sting(Double v, int scale) {
		try {
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(scale);
			format.setMinimumFractionDigits(scale);
			return format.format(v);
		} catch (Exception e) {
			return v.toString();
		}
	}

	/**
	 * 取得指定子串在字符串中出现的次数。
	 *
	 * @param str
	 *            要扫描的字符串
	 * @param strSub
	 *            子字符串
	 * @return 子串在字符串中出现的次数，如果字符串为<code>null</code>或空，则返回<code>0</code>
	 */
	public static int getSubStringCount(String strMain, String strSub) {
		if ((strMain == null) || (strMain.length() == 0) || (strSub == null) || (strSub.length() == 0)) {
			return 0;
		}
		int count = 0;
		int index = 0;

		while ((index = strMain.indexOf(strSub, index)) >= 0) {
			count++;
			index += strSub.length();
		}
		return count;
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static String DoubleFormat(double dbInput, int scale) {
		String strResult = "0";
		// 方式一：
		BigDecimal bd = new BigDecimal(Double.toString(dbInput));
		strResult = bd.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();

		// //方式二：
		// //#.00 表示两位小数 #.0000四位小数 以此类推...
		// String strFormat="#.0";
		// for(int i=1;i<scale;i++){
		// strFormat+="0";
		// }
		// DecimalFormat df = new DecimalFormat(strFormat);
		// strResult = df.format(dbInput);
		//
		// //方式三：
		// //%.2f %. 表示 小数点前任意位数 2 表示两位小数 格式后的结果为f 表示浮点型
		// strFormat="%."+scale +"f";
		// strResult = String.format(Locale.getDefault(),strFormat, dbInput);
		return strResult;
	}

	/********************* 新增加获取图片目录列表和图片列表 ************************************/
	// TODO 获取图片目录列表和图片列表
	// /**
	// * 是否创建了图片集
	// */
	// boolean hasBuildImagesBucketList = false;
	//
	// /**
	// * 得到图片集
	// */
	// public static List<PhotoFolderModel> getPhotoFoldersList1(Context
	// context, String rootPath) {
	// List<PhotoFolderModel> listPhotoFolder = new
	// ArrayList<PhotoFolderModel>();
	// PhotoFolderModel data;
	// try {
	// long startTime = System.currentTimeMillis();
	// // 获取系通图片管理的数据库信息
	// ContentResolver mContentResolver = context.getContentResolver();
	// // 构造相册索引
	// String columns[] = new String[] { BaseColumns._ID,
	// ImageColumns.BUCKET_ID, ImageColumns.PICASA_ID, MediaColumns.DATA,
	// MediaColumns.DISPLAY_NAME, MediaColumns.TITLE, MediaColumns.SIZE,
	// ImageColumns.BUCKET_DISPLAY_NAME };
	// // 条件和分组信息
	// String strSelection = BaseColumns._ID + "!='' " + " and " +
	// MediaColumns.SIZE + " >= 1024" + " and " + ImageColumns.DATA +
	// " is not null and " + ImageColumns.BUCKET_DISPLAY_NAME + " is not null ";
	// if (!TextUtils.isEmpty(rootPath)) {
	// strSelection += " and " + MediaColumns.DATA + " like '" + rootPath +
	// "%' ";
	// }
	// strSelection += ") group by (" + ImageColumns.BUCKET_DISPLAY_NAME;
	//
	// // 按时间降序排序
	// String strOrderBy = MediaColumns.DATE_ADDED + " desc ";
	// // 得到一个游标
	// Cursor cur = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, columns,
	// strSelection, null, strOrderBy);
	// if (cur != null && cur.getCount() > 0) {
	// cur.moveToPrevious();
	// // 获取指定列的索引
	// int photoIDIndex = cur.getColumnIndexOrThrow(BaseColumns._ID);
	// int photoPathIndex = cur.getColumnIndexOrThrow(MediaColumns.DATA);
	// int photoNameIndex =
	// cur.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME);
	// int photoTitleIndex = cur.getColumnIndexOrThrow(MediaColumns.TITLE);
	// int photoSizeIndex = cur.getColumnIndexOrThrow(MediaColumns.SIZE);
	// int bucketDisplayNameIndex =
	// cur.getColumnIndexOrThrow(ImageColumns.BUCKET_DISPLAY_NAME);
	// int bucketIdIndex = cur.getColumnIndexOrThrow(ImageColumns.BUCKET_ID);
	// int picasaIdIndex = cur.getColumnIndexOrThrow(ImageColumns.PICASA_ID);
	// // 获取图片总数
	// int totalNum = cur.getCount();
	// do {
	// String _id = cur.getString(photoIDIndex);
	// String name = cur.getString(photoNameIndex);
	// String path = cur.getString(photoPathIndex);
	// String title = cur.getString(photoTitleIndex);
	// String size = cur.getString(photoSizeIndex);
	// String bucketName = cur.getString(bucketDisplayNameIndex);
	// String bucketId = cur.getString(bucketIdIndex);
	// String picasaId = cur.getString(picasaIdIndex);
	// Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: " + picasaId +
	// " name:" + name + " path:" + path + " title: " + title + " size: " + size
	// + " bucket: " + bucketName + "---");
	// PhotoFolderModel bucket = listPhotoFolder.get(bucketId);
	// if (bucket == null) {
	// bucket = new PhotoFolderModel();
	// bucketList.put(bucketId, bucket);
	// bucket.imageList = new ArrayList<PhotoFileModel>();
	// bucket.bucketName = bucketName;
	// }
	// bucket.count++;
	// PhotoFileModel imageItem = new PhotoFileModel();
	// imageItem.imageId = _id;
	// imageItem.imagePath = path;
	// imageItem.thumbnailPath = thumbnailList.get(_id);
	// bucket.imageList.add(imageItem);
	// } while (cur.moveToNext());
	// }
	// Iterator<Entry<String, PhotoFolderModel>> itr =
	// bucketList.entrySet().iterator();
	// while (itr.hasNext()) {
	// Map.Entry<String, PhotoFolderModel> entry = itr.next();
	// PhotoFolderModel bucket = entry.getValue();
	// Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", " +
	// bucket.count + " ---------- ");
	// for (int i = 0; i < bucket.imageList.size(); ++i) {
	// PhotoFileModel image = bucket.imageList.get(i);
	// Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath + ", " +
	// image.thumbnailPath);
	// }
	// }
	// if (cur != null) {
	// cur.close();
	// }
	// long endTime = System.currentTimeMillis();
	// Log.d(TAG, "getPhotoFolderList useTime: " + (endTime - startTime) +
	// " ms");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return listPhotoFolder;
	// }

	/**
	 * Map按键Key排序(sort by key)
	 *
	 * @author xnjiang
	 * @param oriMap
	 * @return
	 * @since v0.0.1
	 */
	public static Map<Integer, String> sortMapByKey(Map<Integer, String> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<Integer, String> sortedMap = new TreeMap<Integer, String>(new Comparator<Integer>() {
			@Override
			public int compare(Integer key1, Integer key2) {
				int intKey1 = 0, intKey2 = 0;
				try {
					intKey1 = getInt(String.valueOf(key1));
					intKey2 = getInt(String.valueOf(key2));
				} catch (Exception e) {
					intKey1 = 0;
					intKey2 = 0;
				}
				return intKey1 - intKey2;
			}
		});
		sortedMap.putAll(oriMap);
		return sortedMap;
	}

	private static int getInt(String str) {
		int i = 0;
		try {
			Pattern p = Pattern.compile("^\\d+");
			Matcher m = p.matcher(str);
			if (m.find()) {
				i = Integer.valueOf(m.group());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * Map按值Value排序(sort by value)
	 *
	 * @author xnjiang
	 * @param oriMap
	 * @return
	 * @since v0.0.1
	 */
	public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Entry<String, String>> entryList = new ArrayList<Entry<String, String>>(oriMap.entrySet());
			Collections.sort(entryList, new Comparator<Entry<String, String>>() {
				@Override
				public int compare(Entry<String, String> entry1, Entry<String, String> entry2) {
					int value1 = 0, value2 = 0;
					try {
						value1 = getInt(entry1.getValue());
						value2 = getInt(entry2.getValue());
					} catch (NumberFormatException e) {
						value1 = 0;
						value2 = 0;
					}
					return value2 - value1;
				}
			});
			Iterator<Entry<String, String>> iter = entryList.iterator();
			Map.Entry<String, String> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
}
