import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import java.io.*;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("/home/gaborthury/Downloads/CloudUpload-e39c42c36c73.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("SET STORAGE BUCKET HERE")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseApp.initializeApp(options);

        Bucket bucket = StorageClient.getInstance().bucket();
        Storage storage = bucket.getStorage();

        String blobName = "apple.jpg";
        File inputFile = new File("/home/gaborthury/Downloads", blobName);
        BlobId blobId = BlobId.of(bucket.getName(), blobName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //BlobInfo blobInfo = BlobInfo.builder(blobId).contentType("image/jpeg").build();
        BlobInfo blobInfo = BlobInfo
                .newBuilder(bucket.getName(), blobName)
                .setContentType("image/png")
                .build();

        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] buffer = new byte[1024];
            long length = inputFile.length();
            long size = 0;
            long atMoment = 0;
            long atLast = -1;
            int limit;
            while ((limit = inputStream.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
                size += limit;

                atMoment = size * 100 / length;
                if (atMoment != atLast) {
                    System.out.println(atMoment + "%");
                    atLast = atMoment;

                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

