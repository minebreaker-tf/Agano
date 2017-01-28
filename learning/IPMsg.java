/*
 * IP Messenger Main Class
 *		1997/10/3 (C)Copyright T.Kazawa
 */

package ipmsg;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import util.HexDump;
import JP.digitune.util.ByteBuffer;
import JP.digitune.util.ResourceProperties;
import JP.digitune.util.StringReplacer;
import bean.AttachFile;
import bean.Member;

public class IPMsg implements IPMComListener {
	/*========== Constant Value ==========*/
	public static final int IPMSG_DEFAULT_PORT	=	0x0979;
	public static final long IPMSG_COMMASK		= 0x000000ffL;
	public static final long IPMSG_OPTMASK		= 0xffffff00L;
	
	public static final long IPMSG_NOOPERATION	= 0x00000000L;
	
	public static final long IPMSG_BR_ENTRY		= 0x00000001L;
	public static final long IPMSG_BR_EXIT		= 0x00000002L;
	public static final long IPMSG_ANSENTRY		= 0x00000003L;
	public static final long IPMSG_BR_ABSENCE	= 0x00000004L;
	
	public static final long IPMSG_BR_ISGETLIST = 0x00000018L;
	public static final long IPMSG_OKGETLIST	= 0x00000015L;
	public static final long IPMSG_GETLIST		= 0x00000016L;
	public static final long IPMSG_ANSLIST		= 0x00000017L;
	
	public static final long IPMSG_SENDMSG		= 0x00000020L;
	public static final long IPMSG_RECVMSG		= 0x00000021L;
	
	public static final long IPMSG_READMSG		= 0x00000030L;
	public static final long IPMSG_DELMSG		= 0x00000031L;
	
	public static final long IPMSG_GETINFO		= 0x00000040L;
	public static final long IPMSG_SENDINFO		= 0x00000041L;
	
	public static final long IPMSG_GETPUBKEY	= 0x00000072L; //114
	public static final long IPMSG_ANSPUBKEY	= 0x00000073L; //115
	
	// other opt
	public static final long IPMSG_ABSENCEOPT		= 0x00000100L;
	public static final long IPMSG_SERVEROPT		= 0x00000200L;
	public static final long IPMSG_DIALUPOPT		= 0x00010000L;
	public static final long IPMSG_ENCRYPTOPT		= 0x00400000L;
	public static final long IPMSG_ENCRYPTOPTOLD	= 0x00800000L;
	
	// send opt
	public static final long IPMSG_SENDCHECKOPT = 0x00000100L;
	public static final long IPMSG_SECRETOPT	= 0x00000200L;
	public static final long IPMSG_BROADCASTOPT = 0x00000400L;
	public static final long IPMSG_MULTICASTOPT = 0x00000800L;
	public static final long IPMSG_NOPOPUPOPT	= 0x00001000L;
	public static final long IPMSG_AUTORETOPT	= 0x00002000L; // 自動応答（ピンポン防止用）
	public static final long IPMSG_RETRYOPT		= 0x00004000L;
	public static final long IPMSG_PASSWORDOPT	= 0x00008000L;
	public static final long IPMSG_NOLOGOPT		= 0x00020000L;
	public static final long IPMSG_NEWMUTIOPT	= 0x00040000L;

	// encrypt opt
	public static final long IPMSG_RSA_512		= 0x00000001L;
	public static final long IPMSG_RSA_1024		= 0x00000002L;
	public static final long IPMSG_RSA_2048		= 0x00000004L;
	public static final long IPMSG_RC2_40		= 0x00001000L;
	public static final long IPMSG_RC2_128		= 0x00004000L;
	public static final long IPMSG_RC2_256		= 0x00008000L;
	public static final long IPMSG_BLOWFISH_128 = 0x00020000L;
	public static final long IPMSG_BLOWFISH_256 = 0x00040000L;
	
	// file attach opt
	public static final long IPMSG_FILEATTACHOPT = 0x00200000L;
	public static final long IPMSG_GETFILEDATA	 = 0x00000060L;
	public static final long IPMSG_RELEASEFILES  = 0x00000061L;
	public static final long IPMSG_GETDIRFILES   = 0x00000062L;

	// file types for fileattach command
	public static final long IPMSG_FILE_REGULAR  = 0x00000001L;
	public static final long IPMSG_FILE_DIR		 = 0x00000002L;
	public static final long IPMSG_FILE_RETPARENT= 0x00000003L;	// return parent directory
	public static final long IPMSG_FILE_SYMLINK	 = 0x00000004L;
	public static final long IPMSG_FILE_CDEV	 = 0x00000005L;	// for UNIX
	public static final long IPMSG_FILE_BDEV     = 0x00000006L;	// for UNIX
	public static final long IPMSG_FILE_FIFO	 = 0x00000007L;	// for UNIX
	public static final long IPMSG_FILE_RESFORK	 = 0x00000010L;	// for Mac
	
	// file attribute options for fileattach command
	public static final long IPMSG_FILE_RONLYOPT	= 0x00000100L;
	public static final long IPMSG_FILE_HIDDENOPT	= 0x00001000L;
	public static final long IPMSG_FILE_EXHIDDENOPT	= 0x00002000L;	// for MacOS X
	public static final long IPMSG_FILE_ARCHIVEOPT	= 0x00004000L;
	public static final long IPMSG_FILE_SYSTEMOPT	= 0x00008000L;

	// extend attribute types for fileattach command
	public static final long IPMSG_FILE_UID			= 0x00000001L;
	public static final long IPMSG_FILE_USERNAME	= 0x00000002L;	// uid by string
	public static final long IPMSG_FILE_GID			= 0x00000003L;
	public static final long IPMSG_FILE_GROUPNAME	= 0x00000004L;	// gid by string
	public static final long IPMSG_FILE_PERM		= 0x00000010L;	// for UNIX
	public static final long IPMSG_FILE_MAJORNO		= 0x00000011L;	// for UNIX devfile
	public static final long IPMSG_FILE_MINORNO		= 0x00000012L;	// for UNIX devfile
	public static final long IPMSG_FILE_CTIME		= 0x00000013L;	// for UNIX
	public static final long IPMSG_FILE_MTIME		= 0x00000014L;
	public static final long IPMSG_FILE_ATIME		= 0x00000015L;
	public static final long IPMSG_FILE_CREATETIME	= 0x00000016L;
	public static final long IPMSG_FILE_CREATOR		= 0x00000020L;	// for Mac
	public static final long IPMSG_FILE_FILETYPE	= 0x00000021L;	// for Mac
	public static final long IPMSG_FILE_FINDERINFO	= 0x00000022L;	// for Mac
	public static final long IPMSG_FILE_ACL			= 0x00000030L;
	public static final long IPMSG_FILE_ALIASFNAME	= 0x00000040L;	// alias fname
	public static final long IPMSG_FILE_UNICODEFNAME = 0x00000041L;	// UNICODE fname

	public static final int MAX_SOCKBUF		= 65536;
	public static final int MAX_UDPBUF		= 16384;
	public static final int MAX_BUF			= 1024;
	public static final int MAX_CRYPTLEN	= ((MAX_UDPBUF - MAX_BUF) / 2);
	/*========== end ==========*/
	
	public static String OS_NAME;
	private static final String P_FILE = "runtime.properties";
	private ResourceProperties pref = new ResourceProperties("ipmsg.resources");	
	private boolean state = false;
	private InetAddress localaddr;
	private long serial = 0;
	private String user;
	private String host;
	private Hashtable<String, IPMComEvent> userlist = new Hashtable<String, IPMComEvent>();
	private Hashtable<String,IPMAddress> dialupmember = new Hashtable<String, IPMAddress>();
	private Hashtable<IPMListener, IPMListener> ipmlistener = new Hashtable<IPMListener, IPMListener>();
	private Hashtable<Long, NormalSend> recentsend = new Hashtable<Long, NormalSend>();
	private int port;
	private int[] ports;
	private DatagramSocket dsock;
	private int receivecount = 0;
	private IPMProxy proxy;
	private IPMFileServer fileServer;

	private class CryptoInfo {
		private long cap = 0L;
		private PublicKey publickey = null;
		public CryptoInfo(long cap, PublicKey publickey) {
			this.cap = cap;
			this.publickey = publickey;
		}
		public long getCap() {
			return cap;
		}
		public PublicKey getPublicKey() {
			return publickey;
		}
	}
	private boolean hasJCE = true;
	private Hashtable<String, CryptoInfo> publickeys = new Hashtable<String, CryptoInfo>();
	private PublicKey publickey = null;
	private PrivateKey privatekey = null;
	private long getCryptoCaps() {
		return IPMSG_RSA_512 | IPMSG_RSA_1024
			| IPMSG_RC2_40 | IPMSG_RC2_128
			| IPMSG_BLOWFISH_128;
	}
	
	String makeKey(IPMComEvent ipme) {
		IPMsgPacket tmppack = ipme.getPacket();
		String key = ipme.getIPMAddress() + ":"
			+ tmppack.getUser() + ":" + tmppack.getHost();
		return key;
	}
	
	public void receive(IPMComEvent ipme) {
		if (!state)
			return;
		IPMsgPacket packet = ipme.getPacket();
		long opt = packet.getCommand() & IPMSG_OPTMASK;
		
		switch ((int) (packet.getCommand() & IPMSG_COMMASK)) {
		case (int) IPMSG_BR_ENTRY:
			// 新規参加
			String nickname = pref.getProperty("nickName");
			IPMSend.send(dsock, 
					makePack(IPMSG_ANSENTRY | getEntryOpt()	, nickname, true, null), 
					ipme.getIPMAddress()
					);
		case (int) IPMSG_ANSENTRY:
			if (publickey != null)
				IPMSend.send(dsock, 
						makePack(IPMSG_GETPUBKEY, Long.toString(getCryptoCaps(), 16).toUpperCase(), false, null),
						ipme.getIPMAddress()
						);
		case (int) IPMSG_BR_ABSENCE:
			userlist.put(makeKey(ipme), ipme);
			if ((opt & IPMSG_DIALUPOPT) != 0
				&& dialupmember.get(ipme.getIPMAddress().toString()) == null)
				dialupmember.put(ipme.getIPMAddress().toString()
					, ipme.getIPMAddress());
			IPMEvent ie = new IPMEvent(this, IPMEvent.UPDATELIST_EVENT
				, new Date(System.currentTimeMillis()), ipme);
			invokeListener(ie);
			break;
		case (int) IPMSG_BR_EXIT:
			userlist.remove(makeKey(ipme));
			dialupmember.remove(ipme.getIPMAddress().toString());
			publickeys.remove(ipme.getIPMAddress().toString());
			ie = new IPMEvent(this, IPMEvent.UPDATELIST_EVENT
				, new Date(System.currentTimeMillis()), ipme);
			invokeListener(ie);
			break;
		case (int) IPMSG_SENDMSG:
			if ((opt & IPMSG_SENDCHECKOPT) != 0) {
				IPMSend.send(dsock
					, makePack(IPMSG_RECVMSG | IPMSG_AUTORETOPT	, new Long(packet.getNo()).toString(), false, null)
					, ipme.getIPMAddress()
					);
			}
			if (new Boolean(pref.getProperty("absenceState")).booleanValue() && (opt & IPMSG_AUTORETOPT) == 0) {
				try {
					// 不在通知の送信
					String tmpmsg = pref.getProperty("absenceMsg");
					if (!tmpmsg.equals("")) {
						IPMSend.send(dsock
								,makePack(IPMSG_SENDMSG | IPMSG_AUTORETOPT, tmpmsg, false, null) 
								,ipme.getIPMAddress());
					}
				} catch (MissingResourceException ex) {}
			}
			// リストにないユーザから来たメッセージのとき
			if (!userlist.containsKey(makeKey(ipme))) {
				nickname = pref.getProperty("nickName");
				IPMSend.send(dsock
					, makePack(IPMSG_BR_ENTRY | IPMSG_AUTORETOPT | getEntryOpt(), nickname, true, null)
					, ipme.getIPMAddress());
			}
			// 暗号化されているかと
			if ((opt & IPMSG_ENCRYPTOPT) != 0) {
				String decryptData = decryptMessage(packet.getExtra());
//				System.out.println("-------- decryptMessage returns--------");
//				HexDump.dump(decryptData.getBytes());
				// 復号化したデータの終端に0x00がある場合は取り除く
				try {
					byte[] tp = decryptData.getBytes("SJIS");
					int i;
					for ( i=0 ; i < tp.length ; i++) {
						if ( tp[i] == 0x00 ) {
//							System.out.println("find 0x00");
							break;
						}
					}
					byte[] patchData = new byte[i];
					System.arraycopy(tp, 0, patchData, 0, i);
					String s = new String(patchData, "SJIS");
					packet.setExtra(s);
				} catch (Exception e) {}
			}
			// 添付ファイルあり
			if ( (opt & IPMSG_FILEATTACHOPT) != 0 ) {
//				System.out.println("-------- IPMSG_FILEATTACHOPT ---packet.getBytes()--------");
//				HexDump.dump(packet.getBytes());
//				System.out.println("-------- IPMSG_FILEATTACHOPT ---packet.getExtra()--------");
//				HexDump.dump(packet.getExtra().getBytes());
				/*
				送信元UDPポートと同じ番号のTCPポートに対して、IPMSG_GETFILEDATA コマンドを使い、
				拡張部に packetID:fileID:offset を入れて、データ送信要求パケットを出します。（すべてhex）
				 */
			}
			ie = new IPMEvent(this, IPMEvent.RECEIVEMSG_EVENT, new Date(System.currentTimeMillis()), ipme);
			invokeListener(ie);
			break;
		case (int) IPMSG_RECVMSG:
			try {
				Long tmpLong = new Long(packet.getExtra());
				NormalSend ns = (NormalSend) recentsend.get(tmpLong);
				if (ns != null) {
					ns.receiveReply();
					recentsend.remove(tmpLong);
				}
			} catch (NumberFormatException ex) {}
			break;
		case (int) IPMSG_READMSG:
			ie = new IPMEvent(this, IPMEvent.READMSG_EVENT
				, new Date(System.currentTimeMillis()), ipme);
			invokeListener(ie);
			break;
		case (int) IPMSG_DELMSG:
			ie = new IPMEvent(this, IPMEvent.DELETEMSG_EVENT
				, new Date(System.currentTimeMillis()), ipme);
			invokeListener(ie);
			break;
		case (int) IPMSG_GETPUBKEY:
			IPMSend.send(dsock, 
					makePack(IPMSG_ANSPUBKEY, answerPublicKey(ipme.getPacket()), false, null)
					, ipme.getIPMAddress()
					);
			break;
		case (int) IPMSG_ANSPUBKEY:
			receivePublicKey(ipme);
			break;
		case (int) IPMSG_GETINFO:
			IPMSend.send(dsock, makePack(IPMSG_SENDINFO
				, pref.getProperty("version") + " ("
				+ System.getProperty("java.vendor") + " ver."
				+ System.getProperty("java.version") + "/"
				+ System.getProperty("os.name") + " "
				+ System.getProperty("os.version") + ")", false, null)
				, ipme.getIPMAddress());
			break;
		}
	}
	
	String[] cutCString(String cstr) {
		StringTokenizer tokenizer = new StringTokenizer(cstr, ",");
		String[] tmpstrs = new String[tokenizer.countTokens()];
		for (int i = 0; i < tmpstrs.length; i++)
			tmpstrs[i] = tokenizer.nextToken().trim();
		return tmpstrs;
	}
	
	IPMsgPacket makePack(long com, String extra, boolean groupflag, byte[] fileInfo) {
		if (groupflag)
			return new IPMsgPacket(1, getSerial(), user, host, com, extra
				, pref.getProperty("groupName"), fileInfo);
		else
			return new IPMsgPacket(1, getSerial(), user, host, com, extra, null, fileInfo);
	}
	
	synchronized long getSerial() {
		return (System.currentTimeMillis() >> 10) + serial++;
	}
	
	IPMAddress[] getBroadcastAddr() {
		IPMAddress[] tmpaddr;
		int dialup = dialupmember.size();
		try {
			String tmpstr = pref.getProperty("broadcastAddr");
			String[] strbroadcasts = cutCString("255.255.255.255," + tmpstr);
			tmpaddr = new IPMAddress[strbroadcasts.length * ports.length
				+ dialup];
			for (int i = 0; i < strbroadcasts.length; i++)
				for (int j = 0; j < ports.length; j++)
					try {
						tmpaddr[i * ports.length + j]
							= new IPMAddress(ports[j]
								, InetAddress.getByName(strbroadcasts[i]));
					} catch (UnknownHostException ex) {
						tmpaddr[i * ports.length + j] = null;
					}
		} catch (MissingResourceException ex) {
			tmpaddr = new IPMAddress[ports.length + dialup];
			for (int i = 0; i < ports.length; i++)
				try {
					tmpaddr[i] = new IPMAddress(ports[i]
						, InetAddress.getByName("255.255.255.255"));
				} catch (UnknownHostException exx) {
					tmpaddr[i] = null;
				}
		}
		Enumeration<IPMAddress> e = dialupmember.elements();
		for (int i = 0; i < dialup; i++) {
			tmpaddr[i + tmpaddr.length - dialup]= e.nextElement();
		}
		return tmpaddr;
	}
	
	synchronized void invokeListener(IPMEvent tmpevent) {
		for (Enumeration<IPMListener> e = ipmlistener.elements()
			; e.hasMoreElements(); ) {
			IPMListener listener = e.nextElement();
			listener.eventOccured(tmpevent);
		}
	}

	private static String bytesToString(byte[] b) {
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int tmpb = (b[i] < 0) ? ((int) b[i]) + 0x100 : b[i];
			strbuf.append(Integer.toString((int)(tmpb / 16),16).toUpperCase());
			strbuf.append(Integer.toString((int)(tmpb % 16),16).toUpperCase());
		}
		return new String(strbuf);
	}

	byte[] stringToBytes(String src) {
		byte[] buf;
			 buf = new byte[src.length() / 2];
		for (int i = 0; i < src.length(); i += 2) {
			int b = Integer.parseInt(src.substring(i, ((i + 2) > src.length())
				? src.length() : i + 2), 16);
			b = (b > 127) ? b - 0x100 : b;
			buf[i / 2] = (byte) b;
		}
		return buf;
	}
	
	byte[] reverseBytes(byte[] src) {
		for (int i = 0; i < src.length / 2; i++) {
			byte b = src[i];
			src[i] = src[src.length - i - 1];
			src[src.length - i - 1] = b;
		}
		return src;
	}

	String encryptMessage(CryptoInfo ci, String src) {
		if (ci == null || ci.getPublicKey() == null)
			throw new IllegalStateException("publickey unknown.");
		try {
			long flag = 0L;
			int key_length = ci.getPublicKey().getEncoded().length;
			if (key_length > 200)
				flag |= IPMSG_RSA_2048;
			else if (key_length > 100)
				flag |= IPMSG_RSA_1024;
			else
				flag |= IPMSG_RSA_512;
			StringBuffer strbuf = new StringBuffer();
			SecretKey secretkey = null;
			String cipher_name = null;
			if ((ci.getCap() & IPMSG_BLOWFISH_128) != 0) {
				flag |= IPMSG_BLOWFISH_128;
				KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
				kg.init(128);
				secretkey = kg.generateKey();
				cipher_name = pref.getProperty("Cipher2");
			} else if ((ci.getCap() & IPMSG_RC2_128) != 0) {
				flag |= IPMSG_RC2_128;
				KeyGenerator kg = KeyGenerator.getInstance("RC2");
				kg.init(128);
				secretkey = kg.generateKey();
				cipher_name = pref.getProperty("Cipher3");
			} else if ((ci.getCap() & IPMSG_RC2_40) != 0) {
				flag |= IPMSG_RC2_40;
				KeyGenerator kg = KeyGenerator.getInstance("RC2");
				kg.init(40);
				secretkey = kg.generateKey();
				cipher_name = pref.getProperty("Cipher3");
			} else {
				throw new IllegalStateException("no cap!");
			}
			strbuf.append(Long.toString(flag, 16).toUpperCase());
			strbuf.append(":");
			Cipher c = Cipher.getInstance(pref.getProperty("Cipher1"));
			c.init(Cipher.ENCRYPT_MODE, ci.getPublicKey());
			byte[] keydata = c.doFinal(secretkey.getEncoded());
			strbuf.append(bytesToString(keydata));
			strbuf.append(":");
			c = Cipher.getInstance(cipher_name);
			IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			c.init(Cipher.ENCRYPT_MODE, secretkey, iv);
			// 最大長を制限 gosha ////////////////////
			byte[] tp = src.getBytes("SJIS");
			byte[] encryptData;
//			System.out.println("original length:" +  tp.length +" (MAX_CRYPTLEN=" + MAX_CRYPTLEN +")");
			if ( tp.length > MAX_CRYPTLEN) { // MAX_CRYPTLEN = 7680
//				encryptData = Arrays.copyOfRange(tp, 0, MAX_CRYPTLEN);  java 6 のメソッドなので使わない
				encryptData = new byte[MAX_CRYPTLEN];
				System.arraycopy(tp, 0, encryptData, 0, MAX_CRYPTLEN);
			} else {
				encryptData = tp;
			}
//			System.out.println("encryptdata length:" +  encryptData.length);
			byte[] msgdata = c.doFinal(encryptData);
			//////////////////////////// gosha
			strbuf.append(bytesToString(msgdata));
			return new String(strbuf);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return src;
	}

	String decryptMessage(String src) {
		if (privatekey == null)
			return src;
		try {
			StringTokenizer token = new StringTokenizer(src, ":");
			long cap = Long.parseLong(token.nextToken().toLowerCase(), 16);
			String cipher_name = pref.getProperty("Cipher2");
			if ((cap & (IPMSG_BLOWFISH_128 | IPMSG_BLOWFISH_256)) == 0) {
				cipher_name = pref.getProperty("Cipher3");
			}
//			System.out.println(cipher_name);
			byte[] keydata = stringToBytes(token.nextToken());
			byte[] msgdata = stringToBytes(token.nextToken());
//			System.out.println("----decryptMessage----------");
//			HexDump.dump(msgdata);
			Cipher c = Cipher.getInstance(pref.getProperty("Cipher1"));
			c.init(Cipher.DECRYPT_MODE, privatekey);
			byte[] skey = c.doFinal(keydata);
			c = Cipher.getInstance(cipher_name);
			IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			SecretKeySpec sks1 = new SecretKeySpec(skey, 
					pref.getProperty("Cipher2").substring(0, pref.getProperty("Cipher2").indexOf("/"))
				);
			c.init(Cipher.DECRYPT_MODE, sks1, iv);
			byte[] msg = c.doFinal(msgdata);
			return new String(msg, "SJIS");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "decrypt error.";
		}
	}
	
	String answerPublicKey(IPMsgPacket pack) {
		if (publickey == null)
			return "0";
		StringBuffer result = new StringBuffer();
		try {
			result.append(Long.toString(getCryptoCaps(), 16).toUpperCase());
			result.append(":");
			result.append(
				makeRSAPublicKeyString((RSAPublicKey) publickey));
			return new String(result);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}
	}

	void receivePublicKey(IPMComEvent ipme) {
		if (publickey == null)
			return;
		try {
			StringTokenizer token
				= new StringTokenizer(ipme.getPacket().getExtra(), ":");
			long cap = Long.parseLong(token.nextToken(), 16);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keyspec = makePublicKeySpec(token.nextToken());
			PublicKey pubkey = kf.generatePublic(keyspec);
			CryptoInfo ci = new CryptoInfo(cap, pubkey);
			publickeys.put(ipme.getIPMAddress().toString(), ci);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	RSAPublicKeySpec makePublicKeySpec(String src) throws Exception {
		StringTokenizer token = new StringTokenizer(src, "-");
		BigInteger exponent = new BigInteger(token.nextToken(), 16);
		BigInteger modulus = new BigInteger(token.nextToken(), 16);
		return new RSAPublicKeySpec(modulus, exponent);
	}

	RSAPrivateKeySpec makePrivateKeySpec(String src) throws Exception {
		StringTokenizer token = new StringTokenizer(src, "-");
		BigInteger exponent = new BigInteger(token.nextToken(), 16);
		BigInteger modulus = new BigInteger(token.nextToken(), 16);
		return new RSAPrivateKeySpec(modulus, exponent);
	}

	String makeRSAPublicKeyString(RSAPublicKey key) throws Exception {
		return key.getPublicExponent().toString(16).toUpperCase() + "-"
			+ key.getModulus().toString(16).toUpperCase();
	}

	String makeRSAPrivateKeyString(RSAPrivateKey key) throws Exception {
		return key.getPrivateExponent().toString(16).toUpperCase() + "-"
			+ key.getModulus().toString(16).toUpperCase();
	}

	long setupEncryption() {
		if (publickey != null && privatekey != null) {
			return IPMSG_ENCRYPTOPT;
		} else if (!hasJCE)  {
			return 0L;
		}
//		try {
//			Class<?> c = Class.forName(pref.getProperty("jceProvider"));
//			Security.addProvider((Provider) c.newInstance());
//		} catch (Exception ex) {
//			System.err.println("Can't instanciate JCE Provider.->" + ex);
//			hasJCE = false;
//			return 0L;
//		}
		try {
			try {
				KeyFactory kf = KeyFactory.getInstance("RSA");
				RSAPublicKeySpec pubspec
					= makePublicKeySpec(pref.getProperty("publicKey"));
				publickey = kf.generatePublic(pubspec);
				RSAPrivateKeySpec privspec
					= makePrivateKeySpec(pref.getProperty("privateKey"));
				privatekey = kf.generatePrivate(privspec);
			} catch (Exception e) {
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(1024);
				KeyPair kp = kpg.generateKeyPair();
				publickey = kp.getPublic();
				privatekey = kp.getPrivate();
				pref.setProperty("publicKey", makeRSAPublicKeyString(
					(RSAPublicKey) publickey));
				pref.setProperty("privateKey", makeRSAPrivateKeyString(
					(RSAPrivateKey) privatekey));
			}
		} catch (Exception ex) {
//			System.err.println("Can't create key pair.->" + ex);
			return 0L;
		}
		return IPMSG_ENCRYPTOPT;
	}

	long getEntryOpt() {
		long absence = (new Boolean(pref.getProperty("absenceState"))
			.booleanValue()) ? IPMSG_ABSENCEOPT : 0;
		long dialup = (new Boolean(pref.getProperty("dialupState"))
			.booleanValue()) ? IPMSG_DIALUPOPT: 0;
		return absence | dialup | IPMSG_FILEATTACHOPT | setupEncryption();
	}
	
	void makeBroadcastRecvSockets() throws SocketException {
		try {
			String tmpstr = pref.getProperty("ports");
			String[] strports = cutCString(tmpstr);
			ports = new int[strports.length];
			for (int i = 0; i < ports.length; i++) {
				ports[i] = Integer.parseInt(strports[i]);
			}
		} catch (MissingResourceException ex) {
			ports = new int[1];
			ports[0] = IPMSG_DEFAULT_PORT;
		}
		DatagramSocket[] tmpds = new DatagramSocket[ports.length];
		for (int i = 0; i < ports.length; i++) {
			tmpds[i] = new DatagramSocket(ports[i]);
		}
//		dsock = tmpds[0];
		InetAddress proxyaddr = null;
		try {
			String tmpstr = pref.getProperty("proxy");
			proxyaddr = InetAddress.getByName(tmpstr);
		} catch (Exception ex) {
			proxyaddr = null;
		}
		proxy = new IPMProxy(this, proxyaddr, 
				new Boolean(pref.getProperty("proxyBroadcastAll")).booleanValue()
				);
		for (int i = 0; i < ports.length; i++) {
			proxy.addBroadcastPort(ports[i]);
			IPMRecv tmprecv = new IPMRecv(tmpds[i]);
			tmprecv.addIPMComListener(this);
			tmprecv.addIPMComListener(proxy);
			tmprecv.start();
		}
	}
	
	class Child extends Thread {
		private DataInputStream din;
		
		public Child() {
			this.start();
		}
		
		public void run() {
			Socket sock;
			int proxyPort;
			
			try {
				proxyPort = Integer.valueOf(pref.getProperty("proxyPort"));
			} catch (Exception ex) {
				proxyPort = 2426; 
			}

			while (true) {
				try {
					sock = new Socket(localaddr, proxyPort);
					din = new DataInputStream(sock.getInputStream());
				} catch (IOException ex) {
					return;
				}
				byte[] buf = new byte[8192];
				IPMByteBuffer ipmbb = new IPMByteBuffer();
				outer:
				while (true) {
					while (!ipmbb.eop()) {
						int count = 0;
						try {
							count = din.read(buf);
						} catch (IOException ex) {
							break outer;
						}
						if (count == -1)
							break outer;
						ipmbb.append(buf, 0, count);
					}
					buf = ipmbb.getBytes();
					IPMProxyEvent ipmpe = new IPMProxyEvent(this, buf);
					try {
						if (!ipmpe.getToIPMAddress().getInetAddress()
							.equals(InetAddress.getByName("255.255.255.255")))
							continue;
					} catch (UnknownHostException ex) {
						continue;
					}
					IPMComEvent ipmce = new IPMComEvent(this
						, ipmpe.getToIPMAddress().getPort(), ipmpe.getPack()
						, ipmpe.getFromIPMAddress());
					receive(ipmce);
				}
				try {
					makeBroadcastRecvSockets();
					try {
						din.close();
						sock.close();
					} catch (IOException ex) {}
					return;
				} catch (SocketException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public IPMsg() {
		try {
			FileInputStream filein = new FileInputStream(P_FILE);
			pref.load(filein);
			filein.close();
			OS_NAME = System.getProperty("os.name");
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} catch (SecurityException ex) {}
		user = System.getProperty("user.name", "No Name");
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			host = "Unknown Hostname";
		}
		try {
			try {
				localaddr = InetAddress.getByName(pref.getProperty("localAddress"));
			} catch (MissingResourceException ex) {
				localaddr = InetAddress.getLocalHost();
			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		try {
			pref.getProperty("encryption");
			Pattern pattern = Pattern.compile("on", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(pref.getProperty("encryption"));
			hasJCE= matcher.matches();
		} catch (MissingResourceException ex) {
			hasJCE = true;
		}
	}
	
	public String getUser() {
		return user;
	}
	public String getHost() {
		return host;
	}
	public InetAddress getLocalAddress() {
		return localaddr;
	}
	
	public void entry() throws Exception {
		DatagramSocket tmpds;
		Random rand = new Random(System.currentTimeMillis());
		while (true) {
			/* 49154 - 65534 */
			port = (int) (rand.nextLong() % 8192 + 57342);
			try {
				tmpds = new DatagramSocket(port);
			} catch (SocketException ex) {
				continue;
			}
			break;
		}
		dsock = tmpds;
		IPMRecv tmprecv = new IPMRecv(tmpds);
		tmprecv.addIPMComListener(this);
		tmprecv.start();
		try {
			makeBroadcastRecvSockets();
		} catch (SocketException ex) {
			ex.printStackTrace();
			new Child();
		}
		state = true;
		String nickname = pref.getProperty("nickName");
		new BroadcastSend(dsock, 
				makePack(IPMSG_BR_ENTRY | getEntryOpt(), nickname, true, null), 
				getBroadcastAddr()
		);
		fileServer = new IPMFileServerNewIO(this);
//		fileServer = new IPMFileServerClassic(this);
		new Thread(fileServer).start();
	}
	
	public int getPort() {
		return port;
	}
	public synchronized void savePref() {
		try {
			FileOutputStream fileout = new FileOutputStream(P_FILE);
			pref.store(fileout, "IP Messenger Runtime Resource");
		} catch (IOException ex) {}
	}
	
	public synchronized void exit() {
		state = false;
		BroadcastSend bs = new BroadcastSend(dsock, 
					makePack(IPMSG_BR_EXIT, "", false, null),
					getBroadcastAddr()
					);
		try {
			bs.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		savePref();
	}
	
	public synchronized void addIPMListener(IPMListener listener) {
		ipmlistener.put(listener, listener);
	}
	
	public synchronized void removeIPMListener(IPMListener listener) {
		ipmlistener.remove(listener);
	}
	
	public synchronized void setPref(String key, String value) {
		pref.put(key, value);
	}
	
	public synchronized String getPref(String key) {
		return pref.getProperty(key);
	}
	
	public Hashtable<String, IPMComEvent> getUserlist() {
		return userlist;
	}

	public synchronized void incReceiveCount() {
		receivecount++;
	}
	public synchronized void decReceiveCount() {
		receivecount--;
	}
	public boolean lessThanReceiveMax() {
		int recvmax = 500;
		try {
			recvmax = Integer.parseInt(pref.getProperty("receiveMax", "500"));
		} catch (NumberFormatException ex) {}
		return (recvmax > receivecount);
	}

	public void sendMsg(IPMAddress[] addrs, String msg, java.util.List<AttachFile> fileInfoList, long flag) {
		IPMsgPacket ipmp;
		byte[] fileAttachMsg=null;
		if ( fileInfoList != null && fileInfoList.size() != 0 ) {
			try {
			fileAttachMsg = makeFileAttachMsg(fileInfoList);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
//			System.out.println("--------sendMsg()----------------");
//			HexDump.dump(fileInfo);
		}
		if (addrs == null) {
			ipmp = makePack(IPMSG_SENDMSG | IPMSG_BROADCASTOPT | flag, msg, false, fileAttachMsg);
			if ( fileAttachMsg != null ) {
				entryFileInfo2Server(ipmp, fileInfoList);
			}
			new BroadcastSend(dsock, ipmp, getBroadcastAddr());
		} else if (addrs.length == 1) {
			if (publickeys.containsKey(addrs[0].toString())) {
				try {
					msg = encryptMessage(
						(CryptoInfo) publickeys.get(addrs[0].toString()), msg);
					flag |= IPMSG_ENCRYPTOPT;
				} catch (Exception ex) {}
			}
			ipmp = makePack(IPMSG_SENDMSG | IPMSG_SENDCHECKOPT |flag, msg, false, fileAttachMsg);
			if ( fileAttachMsg != null ) {
				entryFileInfo2Server(ipmp, fileInfoList);
			}
			NormalSend ns = new NormalSend(dsock, ipmp, addrs[0]);
			ns.addIPMComListener(new IPMComListener() {
				public void receive(IPMComEvent ipmce) {
					recentsend.remove(new Long(ipmce.getPacket().getNo()));
					IPMEvent ie = new IPMEvent(this, IPMEvent.CANTSENDMSG_EVENT
						, new Date(System.currentTimeMillis()), ipmce);
					invokeListener(ie);
				}
			});
			recentsend.put(new Long(ipmp.getNo()), ns);
			ns.start();
		} else {
			for (int i = 0; i < addrs.length; i++) {
				long tmpflag = flag;
				String tmpmsg = msg;
				if (publickeys.containsKey(addrs[i].toString())) {
					try {
						tmpmsg = encryptMessage(
							(CryptoInfo) publickeys.get(addrs[i].toString())
							, msg);
						tmpflag |= IPMSG_ENCRYPTOPT;
					} catch (Exception ex) {}
				}
				ipmp = makePack(IPMSG_SENDMSG | IPMSG_MULTICASTOPT| tmpflag, tmpmsg, false, fileAttachMsg);
				if ( fileAttachMsg != null ) {
					entryFileInfo2Server(ipmp, fileInfoList);
				}
				IPMSend.send(dsock, ipmp, addrs[i]);
			}
		}
	}
	
	/**
	 * ファイル転送サーバが管理する情報を作成する
	 * @param ipmp
	 * @param fileInfoList
	 */
	private void entryFileInfo2Server(IPMsgPacket ipmp,	List<AttachFile> fileInfoList) {
		List<IPMFileInfo> list = new ArrayList<IPMFileInfo>();
		for ( AttachFile fileinfo : fileInfoList) {
			IPMFileInfo ipmFileInfo = new IPMFileInfo(fileinfo.getFileId(), 
					fileinfo.getDir(), 
					fileinfo.getFilename(),
					fileinfo.getSize(), 
					fileinfo.getMtime(),
					fileinfo.getAttr());
			// IPMFileInfo(String fileId, String dir, String filename, String size, String mtime)
			ipmFileInfo.setPacketId(ipmp.getNo());
			list.add(ipmFileInfo);
		}
		fileServer.add(list);
//		fileServer.check();
	}

	/**
	 * メッセージ送信用のバイト列を作成
	 * @param fileInfoList
	 * @return
	 */
	private byte[] makeFileAttachMsg(List<AttachFile> fileInfoList) throws Exception {
		byte[] separator = { 0x07 }; // \a
		byte[] delimiter = { 0x3A }; // :
		ByteBuffer bb = new ByteBuffer();
		for ( AttachFile fileinfo : fileInfoList) {
			bb.append(fileinfo.getFileId().getBytes()); // fileId
			bb.append(delimiter);
			bb.append(fileinfo.getFilename().replaceAll(":", "::").getBytes("SJIS")); // filename ":"があったら"::"にする
			bb.append(delimiter);
			bb.append(Long.toHexString(Long.valueOf(fileinfo.getSize())).getBytes()); // size
			bb.append(delimiter);
			bb.append(Long.toHexString(Long.valueOf(fileinfo.getMtime())).getBytes()); // mtime
			bb.append(delimiter);
			bb.append(Long.toHexString(Long.valueOf(fileinfo.getAttr())).getBytes());
			bb.append(separator);
		}
		return bb.getBytes();
	}

	public void sendReadMsg(IPMEvent ipme) {
		IPMsgPacket ipmp = makePack(IPMSG_READMSG | IPMSG_AUTORETOPT
			, new Long(ipme.getPacket().getNo()).toString(), false, null);
		IPMSend.send(dsock, ipmp, ipme.getIPMAddress());
	}
	
	public void sendDeleteMsg(IPMEvent ipme) {
		IPMsgPacket ipmp = makePack(IPMSG_DELMSG | IPMSG_AUTORETOPT
			, new Long(ipme.getPacket().getNo()).toString(), false, null);
		IPMSend.send(dsock, ipmp, ipme.getIPMAddress());
	}
	
	public void sendDecryptErrorMsg(IPMEvent ipme) {
		// TODO
	}
	
//	public void getInfo() {
//		BroadcastSend bs = new BroadcastSend(dsock, makePack(IPMSG_GETINFO
//			, "", false), getBroadcastAddr());
//	}
	
	public synchronized void refreshList() {
		IPMAddress[] tmpaddr = getBroadcastAddr();
		userlist = new Hashtable<String, IPMComEvent>();
		dialupmember = new Hashtable<String, IPMAddress>();
		String nickname = pref.getProperty("nickName");
		new BroadcastSend(dsock, 
				makePack(IPMSG_BR_ENTRY | getEntryOpt(), nickname, true, null), 
				tmpaddr);
	}
	
	public void absenceStateChanged() {
		String nickname = pref.getProperty("nickName");
		new BroadcastSend(dsock, 
				makePack(IPMSG_BR_ABSENCE | getEntryOpt(), nickname, true, null),
				getBroadcastAddr());
	}
	
	public String makeDateStr(Date now) {
		SimpleDateFormat dateformatter
			= new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		dateformatter.setTimeZone(
			DateFormat.getDateInstance().getTimeZone());
		return "at " + dateformatter.format(now);
	}
	
	public String makeListStr(IPMsgPacket aPack) {
		String tmpuser = null;
		if (aPack.getExtra() == null)
			tmpuser = aPack.getUser();
		else
			tmpuser = aPack.getExtra();
		StringBuffer strbuf = new StringBuffer();
		if ((aPack.getCommand() & IPMsg.IPMSG_ABSENCEOPT) != 0)
			tmpuser = tmpuser + "*";
		strbuf.append(tmpuser + " (");
		if (aPack.getGroup() != null)
			strbuf.append(aPack.getGroup() + "/");
		strbuf.append(aPack.getHost() + ")");
		return new String(strbuf);
	}

	public Member makeMember(IPMsgPacket aPack) {
		String tmpuser = null;
		if (aPack.getExtra() == null)
			tmpuser = aPack.getUser();
		else
			tmpuser = aPack.getExtra();
		Member member = new Member();
		if ((aPack.getCommand() & IPMsg.IPMSG_ABSENCEOPT) != 0) {
			tmpuser = tmpuser + "*";
		}
		member.setName(tmpuser);
		if (aPack.getGroup() != null) {
			member.setGroup(aPack.getGroup());
		}
		member.setHost(aPack.getHost() );
		return member;
	}

	public String[] makeListStrs(IPMsgPacket aPack) {
		String tmpuser = "", tmpgroup = "", tmphost = "";
		if (aPack.getExtra() == null)
			tmpuser = aPack.getUser();
		else
			tmpuser = aPack.getExtra();
		if ((aPack.getCommand() & IPMsg.IPMSG_ABSENCEOPT) != 0)
			tmpuser = tmpuser + "*";
		if (aPack.getGroup() != null)
			tmpgroup = aPack.getGroup();
		tmphost = aPack.getHost();
		String[] result = { tmpuser, tmpgroup, tmphost };
		return result;
	}

	public String makeListStr(String auser, String agroup, String ahost) {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(auser);
		strbuf.append(" (");
		if (agroup != null && !agroup.equals("")) {
			strbuf.append(agroup);
			strbuf.append("/");
		}
		strbuf.append(ahost);
		strbuf.append(")");
		return new String(strbuf);
	}
	
	public synchronized void writeLog(String str1, String str2, String body) {
		String cr = System.getProperty("line.separator", "\n");
		try {
			String logname = pref.getProperty("logFilename");
			FileWriter fw = new FileWriter(logname, true);
			String tmpstr = "=====================================" + cr;
			fw.write(tmpstr, 0, tmpstr.length());
			tmpstr = " " + str1 + cr;
			fw.write(tmpstr, 0, tmpstr.length());
			tmpstr = "	" + str2 + cr;
			fw.write(tmpstr, 0, tmpstr.length());
			tmpstr = "-------------------------------------" + cr;
			fw.write(tmpstr, 0, tmpstr.length());
			tmpstr = StringReplacer.replaceString(body, "\n", cr) + cr + cr;
			fw.write(tmpstr, 0, tmpstr.length());
			fw.close();
		} catch (MissingResourceException ex) {
		} catch (IOException ex) {}
	}
}
