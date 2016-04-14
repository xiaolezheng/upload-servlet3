package com.lxz.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

/**
 * servlet3.0 实现文件上传
 *
 * Created by xiaolezheng on 16/4/14.
 */

@WebServlet(description = "文件上传", urlPatterns = { "/upload" })
@MultipartConfig(location = "/tmp/upload", // 文件存放路径，指定的目录必须存在，否则会抛异常
maxFileSize = 8388608, // 最大上传文件大小,字节为单位
fileSizeThreshold = 819200 // 当数据量大于该值时，内容将被写入文件。（specification中的解释的大概意思，不知道是不是指Buffer size），大小也是已字节单位
)
public class FileUploadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

    private static final String fileNameExtractorRegex = "filename=\".+\"";
    private static final Pattern PATTERN = Pattern.compile(fileNameExtractorRegex);
    private static final String CHARSET = "UTF-8";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(CHARSET);
        response.setCharacterEncoding(CHARSET);
        Writer writer = new OutputStreamWriter(response.getOutputStream());

        try {

            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String fileName = getFileName(part);
                if (StringUtils.isNotEmpty(fileName)) {
                    logger.info("fileName: {}", fileName);

                    String newFileName = buildFileName(fileName);

                    String subDir = StringUtils.substring(newFileName, 0, 4);

                    File file = new File("/tmp/upload/" + subDir);
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    part.write(subDir + "/" + newFileName);

                    writer.write("上传成功,fileUrl:" + subDir + "/" + newFileName);
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            writer.write("上传失败" + e.getMessage());
        } finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * 从Part的Header信息中提取上传文件的文件名
     * 
     * @param part
     * @return 上传文件的文件名，如果如果没有则返回null
     */
    private String getFileName(Part part) {
        // 获取header信息中的content-disposition，如果为文件，则可以从其中提取出文件名
        String contentDesc = part.getHeader("content-disposition");
        String fileName = null;

        Matcher matcher = PATTERN.matcher(contentDesc);
        if (matcher.find()) {
            fileName = matcher.group();
            fileName = fileName.substring(10, fileName.length() - 1);
        }

        return fileName;
    }

    private String buildFileName(String sourceFileName) {
        String newFileName = DigestUtils.md5Hex(sourceFileName);

        String fileExtension = Files.getFileExtension(sourceFileName);

        return Joiner.on(".").join(newFileName, fileExtension);
    }

}
