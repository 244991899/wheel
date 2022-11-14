package com.easy.wheel.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description  文件上传使用包
 *
 *     pom 依赖
 * 		<dependency>
 * 			<groupId>com.jcraft</groupId>
 * 			<artifactId>jsch</artifactId>
 * 			<version>0.1.54</version>
 * 		</dependency>
 *      使用第三方jar 包， 快捷实现sftp 上传下载功能
 *      开发速度直接起飞
 * @Author Mr.Yuxd
 * @Date 2022/11/14
 * @Version 1.0
 */
@Slf4j
public class SftpUtils {
    /**
     * @param filePath 上传文件路径
     * @param ftpPath  上传到的sftp服务器目录
     * @param username 用户名
     * @param password 密码
     * @param host     ip
     * @param port     端口
     */
    public static void uploadFile(String filePath, String ftpPath, String username, String password, String host, Integer port) {
        FileInputStream input;
        ChannelSftp sftp;
        try {
            JSch jsch = new JSch();
            //获取ssh - session
            com.jcraft.jsch.Session sshSession = jsch.getSession(username, host, port);
            //添加配置密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //关闭主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启session连接
            sshSession.connect();
            //获取sftp通道-并连接
            sftp = (ChannelSftp) sshSession.openChannel("sftp");
            sftp.connect();
            //文件乱码处理
            /*Class<ChannelSftp> c = ChannelSftp.class;
            Field f = c.getDeclaredField("server_version");
            f.setAccessible(true);
            f.set(sftp, 2);
            sftp.setFilenameEncoding("GBK");*/
            //判断目录是否存在
            try {
                //ls()得到指定目录下的文件列表
                Vector dir = sftp.ls(ftpPath);
            } catch (SftpException e) {
                sftp.mkdir(ftpPath);
            }
            sftp.cd(ftpPath);
            String filename = filePath.substring(filePath.lastIndexOf(File.separator) + 1); //附件名字
            input = new FileInputStream(new File(filePath));
            sftp.put(input, filename);
            input.close();
            sftp.disconnect();
            sshSession.disconnect();
            log.info("==============你的上传成功了===============");
        } catch (Exception e) {
            log.error("==============你的上传成功了===============",e);
        }
    }

    /**
     * @param directory    SFTP服务器的文件路径
     * @param downloadFile SFTP服务器上的文件名
     * @param saveFile     保存到本地路径
     * @param username     用户
     * @param password     密码
     * @param host         ip
     * @param port         端口
     */
    public static void downloadFile(String directory, String downloadFile, String saveFile, String username, String password, String host, Integer port) {
        ChannelSftp sftp;
        try {
            JSch jsch = new JSch();
            //获取ssh - session
            com.jcraft.jsch.Session sshSession = jsch.getSession(username, host, port);
            //设置密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //关闭主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启session连接
            sshSession.connect();
            //获取sftp通道-并连接
            sftp = (ChannelSftp) sshSession.openChannel("sftp");
            sftp.connect();
            if (directory != null && !"".equals(directory)) {
                sftp.cd(directory);
            }
            FileOutputStream output = new FileOutputStream(new File(saveFile));
            sftp.get(downloadFile, output);
            output.close();
            sftp.disconnect();
            sshSession.disconnect();
            log.info("================你的下载成功了！==================");
        } catch (Exception e) {
            log.error("================你的文件下载异常了！================", e);
        }
    }

}