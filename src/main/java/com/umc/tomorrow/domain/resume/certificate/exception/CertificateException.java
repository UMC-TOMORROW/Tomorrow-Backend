package com.umc.tomorrow.domain.resume.certificate.exception;

import com.umc.tomorrow.domain.resume.certificate.exception.code.CertificateErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class CertificateException extends RestApiException {
    public CertificateException(CertificateErrorStatus certificateErrorStatus) {
        super(certificateErrorStatus);
    }
}
