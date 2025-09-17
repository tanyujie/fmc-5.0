package com.paradisecloud.fcm.ops.utils;

import com.jcraft.jsch.*;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Vector;

/**
 * 只用于MTR探测
 */
public abstract class SshRemoteServerOperateForFMEOPS
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SshRemoteServerOperateForFMEOPS.class);
	private Session session;// JSCH session
	private boolean logined = false;// 是否登陆

	private static final SshRemoteServerOperateForFMEOPS INSTANCE = new SshRemoteServerOperateForFMEOPS()
	{

	};

	public static SshRemoteServerOperateForFMEOPS getInstance()
	{
		return INSTANCE;
	}

	/**
	 * 远程登陆
	 * 
	 * @throws Exception
	 */
	public void sshRemoteCallLogin(String ipAddress, String userName, String password, Integer port) throws Exception 
	{
		// 如果登陆就直接返回
		if (logined) 
		{
			return;
		}
		
		// 创建jSch对象
		JSch jSch = new JSch();
		try 
		{
			// 获取到jSch的session, 根据用户名、主机ip、端口号获取一个Session对象
			session = jSch.getSession(userName, ipAddress, port);
			
			// 设置密码
			session.setPassword(password);

			//通过Session建立连接
			 session.setConfig(FcmConfigConstant.STRICT_HOST_KEY_CHECKING, FcmConfigConstant.NO);
			 session.connect();
			 logined = true;
		} 
		catch (JSchException e) 
		{
			// 设置登陆状态为false
			logined = false;
			LOGGER.error("主机登录失败, IP = " + ipAddress + ", USERNAME = " + userName + ", Exception:" , e.getMessage());
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeSession() 
	{
		// 调用session的关闭连接的方法
		if (session != null) 
		{
			// 如果session不为空,调用session的关闭连接的方法
			session.disconnect();
			logined = false;
		}

	}
	
	
	/**
	 * 执行相关nfs的相关命令
	 * 
	 * @param command
	 * @throws IOException
	 */
	public void onlyExecCommand(String command) throws IOException 
	{
		// 定义channel变量
		Channel channel = null;
		try 
		{
			// 如果命令command不等于null
			if (StringUtils.isNotEmpty(command)) 
			{
				// 打开channel
				// 说明：exec用于执行命令;sftp用于文件处理
				channel = session.openChannel(FcmConfigConstant.EXEC);
				ChannelExec channelExec = (ChannelExec) channel;
				
				// 设置command
				channelExec.setCommand(command);
			}
		} 
		catch (JSchException e) 
		{
			LOGGER.error("执行命令失败!" , e.getMessage());
		} 
		catch (Exception e) 
		{
			LOGGER.error("执行命令失败!" , e.getMessage());
		} 
		finally 
		{
			if (null != channel) 
			{
				channel.disconnect();
			}
		}
	}

	/**
	 * 执行相关的命令
	 * 
	 * @param command
	 * @throws IOException
	 */
	public String execCommand(String command) throws IOException 
	{
		// 定义channel变量
		Channel channel = null;
		String processDataStream = null;
		try 
		{
			// 如果命令command不等于null
			if (StringUtils.isNotEmpty(command)) 
			{
				// 打开channel
				// 说明：exec用于执行命令;sftp用于文件处理
				channel = session.openChannel(FcmConfigConstant.EXEC);
				ChannelExec channelExec = (ChannelExec) channel;
				
				// 设置command
				channelExec.setCommand(command);
				
				channelExec.setInputStream(null);
				
				// 执行相关的命令
				processDataStream = processDataStream(channelExec);
				
			}
		} 
		catch (JSchException e) 
		{
			LOGGER.error("执行命令失败!" , e.getMessage());
		} 
		catch (IOException e) 
		{
			LOGGER.error("执行命令失败!" , e.getMessage());
		} 
		catch (Exception e) 
		{
			LOGGER.error("执行命令失败!" , e.getMessage());
		} 
		finally 
		{
			if (null != channel) 
			{
				channel.disconnect();
			}
		}
		return processDataStream;
	}

	/**
	 * 对将要执行的linux的命令进行遍历
	 * 
	 * @param channelExec
	 * @return
	 * @throws Exception
	 */
	public String processDataStream(ChannelExec channelExec) throws Exception 
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

		// channel进行连接
		channelExec.connect();
		
		String result = null;
		try 
		{
			while ((result = br.readLine()) != null) 
			{
				sb.append(result + "\n");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("获取数据流失败!" , e.getMessage());
		} 
		finally 
		{
			br.close();
		}
		return sb.toString();
	}

	/**
	 * 上传文件 可参考:https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html
	 * 
	 * @param directory  上传文件的目录
	 * @param uploadFile 将要上传的文件
	 */
	public void uploadFile(String directory, String uploadFile) 
	{	
		ChannelSftp channelSftp = null;
		try 
		{
			// 打开channelSftp
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			
			// 远程连接
			channelSftp.connect();

			File dst = new File(directory);
			if(!dst.exists()){
				// 创建一个文件名称问uploadFile的文件
				File file = new File(uploadFile);

				// 将文件进行上传(sftp协议)
				// 将本地文件名为src的文件上传到目标服务器,目标文件名为dst,若dst为目录,则目标文件名将与src文件名相同.
				// 采用默认的传输模式:OVERWRITE
				channelSftp.put(new FileInputStream(file), directory, ChannelSftp.OVERWRITE);
			}
			// 切断远程连接
//			channelSftp.exit();
		} 
		catch (JSchException e) 
		{
			LOGGER.error("上传文件失败!" , e.getMessage());
		} 
		catch (SftpException e) 
		{
			LOGGER.error("上传文件失败!" , e.getMessage());
		} 
		catch (FileNotFoundException e) 
		{
			LOGGER.error("未发现上传文件!" , e.getMessage());
		}finally {
			if(null != channelSftp) {
				// 切断远程连接
				channelSftp.exit();
			}
		}

	}

	/**
	 * 下载文件 采用默认的传输模式：OVERWRITE
	 * 
	 * @param src linux服务器文件地址
	 * @param dst 本地存放地址
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void fileDownload(String src, String dst) throws JSchException, SftpException 
	{
		// src 是linux服务器文件地址,dst 本地存放地址
		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		
		// 远程连接
		channelSftp.connect();
		
		// 下载文件,多个重载方法
		channelSftp.get(src, dst);
		
		// 切断远程连接,quit()等同于exit(),都是调用disconnect()
		channelSftp.quit();
		
		// channelSftp.disconnect();
	}

	/**
	 * 删除文件
	 *
	 * @param directoryFile  要删除文件所在目录
	 * @throws SftpException
	 * @throws JSchException
	 */
	public void deleteFile(String directoryFile) throws SftpException, JSchException 
	{
		// 打开openChannel的sftp
		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		
		// 远程连接
		channelSftp.connect();
		
		// 删除文件
		channelSftp.rm(directoryFile);
		
		// 切断远程连接
		channelSftp.exit();
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory 要列出的目录
	 * @return
	 * @throws SftpException
	 * @throws JSchException
	 */
	@SuppressWarnings("unchecked")
	public Vector listFiles(String directory) throws JSchException, SftpException 
	{
		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		
		// 远程连接
		channelSftp.connect();
		
		// 显示目录信息
		Vector ls = channelSftp.ls(directory);
		
		// 切断连接
		channelSftp.exit();
		return ls;
	}
	
	/**
	 * 文件重命名
	 * @param directory
	 * @param oldname
	 * @param newname
	 * @param sftp
	 */
	public void renameFile(String directory, String oldname, String newname,ChannelSftp sftp) 
	{
		try 
		{
			sftp.cd(directory);
			sftp.rename(oldname, newname);
		} 
		catch (Exception e) 
		{
			LOGGER.error("文件重命名失败!" , e.getMessage());
		}
	}

	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}

	
}
