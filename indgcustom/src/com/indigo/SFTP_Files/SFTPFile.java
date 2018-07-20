package com.indigo.SFTP_Files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPFile {
	
	public static Properties prop = new Properties();
	public static InputStream input = null;
	public static JSch jsch;
	public static Session session = null;
	public static Channel channel = null;
	public static ChannelSftp channelSftp = null;
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static void main(String a[]) throws IOException {
		System.out.println("SFTP File Copy STARTS::::"+ dateFormat.format(new Date()));
		// load a properties file
		input = new FileInputStream("/data/testscript/java/sftpIndigo.properties");
		prop.load(input);
		try {
			establishSFTPConnection();
			downloadFiles();
		} catch (Exception ex) {
	        System.out.println("Exception found while tranfer the response.");
	        ex.printStackTrace();
	   }
	   //Closing the SFTP Connection
	   finally{
	       channelSftp.exit();
	       System.out.println("SFTP Channel exited.");
	       channel.disconnect();
	       System.out.println("Channel disconnected.");
	       session.disconnect();
	       System.out.println("Host Session disconnected.");
	       System.out.println("SFTP File Copy ENDS::::"+ dateFormat.format(new Date()));
	   }
	}
	
	private static void establishSFTPConnection() throws JSchException {
		String SFTPHOST = prop.getProperty("SFTP_HOST");
        int SFTPPORT = Integer.valueOf(prop.getProperty("SFTP_PORT"));
        String SFTPUSER = prop.getProperty("SFTP_USER");
        String SFTPPASS = prop.getProperty("SFTP_PASS");
        
        jsch = new JSch();
        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
        session.setPassword(SFTPPASS);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        System.out.println("Host connected.");
        channel = session.openChannel("sftp");
        channel.connect();
        System.out.println("SFTP channel opened and connected.");
        channelSftp = (ChannelSftp) channel;
	}
	
	private static void downloadFiles () throws SftpException, IOException, InterruptedException {
        // get the property value and print it out
        String folders = prop.getProperty("FOLDERS");
        List<String> dirList= Arrays.asList(folders.split(","));
        
        
        for(String workingDir : dirList) {
        	System.out.println("workingDir:::"+workingDir);
        	String workingDirName = workingDir.substring(workingDir.lastIndexOf("/")+1);
        	String destFileName, unzipCmd, renameCmd;
        	int counter = 0;
        	
        	String isSplitEnabled = prop.getProperty(workingDirName+"_SPLIT_ENABLED") != null ? 
        										prop.getProperty(workingDirName+"_SPLIT_ENABLED") : "N";
        	int no_folders = prop.getProperty(workingDirName+"_NO_FOLDERS") != null ?
        						Integer.valueOf(prop.getProperty(workingDirName+"_NO_FOLDERS")) : 1;         	
        	
            channelSftp.cd(workingDir);
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.done");
            //Validating sempahore file is present or not

            if(!list.isEmpty()) {
            	System.out.println("Sempahore file present:::");
                //Validating GZ input files is present or not
            	Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls("*.gz");
            	if(!fileList.isEmpty()) {
	            	for(ChannelSftp.LsEntry fileEntry : fileList) {
	            		//Condition to download files to multiple folders only for "2" - Can be configured using properties later
	            		if(isSplitEnabled.equalsIgnoreCase("Y")) {
	                		counter++;
	                		int folderToCopy = counter%no_folders == 0 ? no_folders : counter%no_folders;
	                		destFileName = "/data/testscript/java/download/" + workingDirName + "_" + folderToCopy + "/"+ fileEntry.getFilename();
	                	}else {
	                		destFileName = "/data/testscript/java/download/" + workingDirName + "/"+ fileEntry.getFilename();
	                	}
	        			
	        			//Downloading the file with and naming it with .tmp extension
	        			channelSftp.get(fileEntry.getFilename(), destFileName+".tmp");
	        			
	        			//Once download is completing, renaming the .tmp to actual file name .gz
	        			renameCmd = "mv " + destFileName +".tmp" + " " + destFileName;
	        			ProcessBuilder pb = new ProcessBuilder("/usr/bin/bash", "-c", renameCmd);
	        			Process p = pb.start();
	        			p.waitFor();
	        			
	        			//Unzip the gzip files
	        			unzipCmd = "gzip -d " + destFileName;
	        			ProcessBuilder pb1 = new ProcessBuilder("/usr/bin/bash", "-c", unzipCmd);
	        			Process p1 = pb1.start();
	        			p1.waitFor();
	        			
	        			//Removing the .gz files from SFTP server
	        			channelSftp.rm(fileEntry.getFilename());
	            	}
	            	//Removing the semaphore file from SFTP server
	            	channelSftp.rm("execution.done");
	            	System.out.println("Deleted SEMAPHORE file from server::");
            	}else {
                	System.out.println("No Input file present:::"+workingDir);
                }
            }else {
            	System.out.println("No Sempahore file present:::"+workingDir);
            }
        }   
    }
}
