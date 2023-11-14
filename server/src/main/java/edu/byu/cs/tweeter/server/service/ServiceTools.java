package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.factory.AuthDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FeedsDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.StoriesDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FactoryInterface;

public class ServiceTools {
    protected FactoryInterface factory;

    protected AuthDAOInterface authDAO;
    protected FollowDAOInterface followingDAO;
    protected FeedsDAOInterface feedsDAO;
    protected StoriesDAOInterface storiesDAO;
    protected UserDAOInterface userDAO;

    // S3 client
    AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-east-1")
            .build();

    AuthDAOInterface getAuthDAO() {
        if (authDAO == null) {
            authDAO = factory.createAuthDAO();
        }
        return authDAO;
    }

    FeedsDAOInterface getFeedsDAO() {
        if (feedsDAO == null) {
            feedsDAO = factory.createFeedsDAO();
        }
        return feedsDAO;
    }

    FollowDAOInterface getFollowingDAO() {
        if (followingDAO == null) {
            followingDAO = factory.createFollowDAO();
        }
        return followingDAO;
    }

    StoriesDAOInterface getStoriesDAO() {
        if (storiesDAO == null) {
            storiesDAO = factory.createStoriesDAO();
        }
        return storiesDAO;
    }

    UserDAOInterface getUserDAO() {
        if (userDAO == null) {
            userDAO = factory.createUserDAO();
        }
        return userDAO;
    }

    protected String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }

    public String getMD5Hash(final String input) {
        try {
            String hashtext = null;
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Compute message digest of the input
            byte[] messageDigest = md.digest(input.getBytes());

            hashtext = convertToHex(messageDigest);

            return hashtext;
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    protected AuthToken generateNewAuthToken() {
        SecureRandom secureRandom = new SecureRandom(); //threadsafe
        Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token =  base64Encoder.encodeToString(randomBytes);

        @SuppressWarnings("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()); System.out.println(timeStamp);


        return new AuthToken(token, timeStamp);
    }

    protected void insertImageIntoS3(String imageUrl, String filename) {
        byte[] byteArray = Base64.getDecoder().decode(imageUrl);
        ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");
        PutObjectRequest request = new PutObjectRequest("my-bucket-brendanizu", filename, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);
    }

    protected String getImageFromS3(String filename) {
        URL url = s3.getUrl("my-bucket-brendanizu", filename);
        return url.toString();
    }

    protected void insertMessageIntoSQS(String message, String url) {
        SendMessageRequest messageRequest = new SendMessageRequest()
                .withQueueUrl(url)
                .withMessageBody(message);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(messageRequest);
    }
}
