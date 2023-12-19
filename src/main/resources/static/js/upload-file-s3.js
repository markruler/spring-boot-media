// AWS Config
const bucket_region = "ap-northeast-2";
const bucket_name = "";
const aws_access_key_id = "";
const aws_secret_access_key = "";
// Initialize the Amazon Cognito credentials provider
// AWS.config.credentials = new AWS.CognitoIdentityCredentials({IdentityPoolId: 'IDENTITY_POOL_ID'});

const s3 = new AWS.S3({
    region: bucket_region,
    accessKeyId: aws_access_key_id,
    secretAccessKey: aws_secret_access_key,
    params: {Bucket: bucket_name}
});

/**
 * chunk file 비동기 전송 전처리 to S3
 */
function send_file_s3_multipart() {
    document.getElementById("current-progress").value = 0;
    show_loading();
    alert('AWS Cognito 사용, 간단한 데모를 위해 Credentials 하드코딩');

    // uploadId 발급
    const params = {
        Bucket: bucket_name,
        Key: 'videos/demo/example.mp4'
    };

    s3.createMultipartUpload(params, function (err, data) {
        if (err) {
            console.log("Error", err);
            return;
        }
        const uploadId = data.UploadId;
        console.log("UploadId:", uploadId);
        const file = document.getElementById("video-file").files[0];

        // software.amazon.awssdk.services.s3.model.S3Exception: Part number must be an integer between 1 and 10000
        let current_index = 1;
        let etags = [];
        send_next_s3_multipart_async(uploadId, current_index, etags, file);
    });
}

/**
 * chunk file을 비동기로 요청하기 위한 Promise 목록
 * @param uploadId
 * @param current_index
 * @param etags
 * @param file
 * @returns {Promise<*[]>}
 */
function send_next_s3_multipart_async(uploadId, current_index, etags, file) {
    const ONE_MB = 1024 * 1024;
    // software.amazon.awssdk.services.s3.model.S3Exception:
    // Your proposed upload is smaller than the minimum allowed size
    // minimum size: 5MB
    const chunk_size = 10 * ONE_MB;
    const total_chunks = Math.ceil(file.size / chunk_size);
    if (current_index > total_chunks) {
        return;
    }
    console.log(`current_index(${current_index}) > total_chunks(${total_chunks})`);

    const start = (current_index - 1) * chunk_size;
    const chunk = file.slice(start, Math.min(start + chunk_size, file.size));
    const uploadPart_params = {
        Body: chunk, // chunk 사이즈로 자른 blob
        Bucket: bucket_name,
        Key: 'videos/demo/example.mp4',
        PartNumber: current_index, // 1 ~ 10,000
        UploadId: uploadId // s3.createMultipartUpload 함수에서 받은 id
    };
    s3.uploadPart(uploadPart_params, function (err, data) {
        if (err) {
            console.log("Error", err);
        } else {
            console.log("ETag:", data);
            etags.push({
                ETag: data.ETag,
                PartNumber: current_index
            });
            update_progress(total_chunks);
        }
        console.log(etags);
        send_next_s3_multipart_async(uploadId, current_index + 1, etags, file);

        // 업로드 완료 후 알림 및 조합 처리
        if (current_index === total_chunks) {
            complete_s3_multipart_async(uploadId, etags);
        }
    });
}

function update_progress(total_chunks) {
    const previous_progress = document.getElementById("current-progress").value;
    // console.log(`previous_progress: ${previous_progress}`);
    const current_progress = Number(previous_progress) + 1;
    document.getElementById("current-progress").value = current_progress;
    // console.log(`progress: ${current_progress}`);
    let percent = Math.floor(current_progress / total_chunks * 100) + "%";
    // console.log(`percent: ${percent}`);
    document.getElementById("result").textContent = percent;
}

function complete_s3_multipart_async(
    uploadId,
    etags,
) {
    // await sleep(1_000);
    console.debug(`complete... uploadId: ${uploadId}`);
    console.debug(`complete... etags: ${etags}`);

    const params = {
        Bucket: bucket_name,
        Key: 'videos/demo/example.mp4',
        MultipartUpload: {
            Parts: etags
        },
        UploadId: uploadId
    };
    s3.completeMultipartUpload(params, function (err, data) {
        if (err) {
            console.log("Error", err);
        } else {
            console.log("Upload Success", data.Location);
            alert("Upload Success");
            hide_loading();
        }
    });
}

function sleep(ms) {
    console.debug(`sleeping ${ms} ms...`);
    return new Promise((r) => setTimeout(r, ms));
}
