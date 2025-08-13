package com.umc.tomorrow.domain.email.enums;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;

public enum EmailContentType implements EmailContentProvider {
    JOB_APPLY {
        @Override
        public String getSubject(User user, Job job) {
            return "[지원 완료] " + user.getName() + "님이 " + job.getTitle() + "에 지원했습니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return "정상적으로 지원이 완료되었습니다.";
        }
    },
    
    JOB_ACCEPTED {
        @Override
        public String getSubject(User user, Job job) {
            return "[내일] 전형 결과 안내드립니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return createResultEmailContent(user, job, true);
        }
    },
    
    JOB_REJECTED {
        @Override
        public String getSubject(User user, Job job) {
            return "[내일] 전형 결과 안내드립니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return createResultEmailContent(user, job, false);
        }
    };
    
    //여기에 이어서 추가해주시면 됩니다.
    
    /**
     * 지원 결과 메일 HTML 템플릿 생성
     */
    private static String createResultEmailContent(User user, Job job, boolean isAccepted) {
        String resultMessage = isAccepted ? 
            "축하합니다! 이번 전형에서 합격하셨습니다." :
            "아쉽지만 이번 전형에서는 함께하지 못하게 되었습니다.";
            
        String closingMessage = isAccepted ?
            "앞으로 함께 일할 수 있게 되어 기쁩니다." :
            "합격 소식을 전해드리지 못했지만 앞으로의 여정을 응원합니다.";
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>전형 결과 안내</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .email-container { max-width: 600px; margin: 0 auto; background-color: #4CAF50; border-radius: 10px; padding: 30px; }
                    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
                    .title { color: #1B5E20; font-size: 24px; font-weight: bold; }
                    .logo { width: 60px; height: 60px; background-color: white; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
                    .logo-text { color: #4CAF50; font-weight: bold; font-size: 14px; }
                    .content { color: #E8F5E8; line-height: 1.6; margin-bottom: 30px; }
                    .info-section { margin-top: 30px; }
                    .info-title { color: #E8F5E8; font-weight: bold; margin-bottom: 15px; }
                    .info-table { width: 100%; border-collapse: collapse; }
                    .info-table td { padding: 12px; border-bottom: 1px solid #E8F5E8; color: #E8F5E8; }
                    .info-table td:first-child { font-weight: bold; width: 40%; }
                    .result-message { font-size: 18px; font-weight: bold; margin: 20px 0; text-align: center; }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="title">전형 결과 안내</div>
                        <div class="logo">
                            <div class="logo-text">내일</div>
                        </div>
                    </div>
                    
                    <div class="content">
                        <p>안녕하세요, %s님</p>
                        <div class="result-message">%s</div>
                        <p>%s</p>
                        <p>감사합니다.</p>
                    </div>
                    
                    <div class="info-section">
                        <div class="info-title">* 지원 기업 정보</div>
                        <table class="info-table">
                            <tr>
                                <td>지원공고명</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>지원회사명</td>
                                <td>%s</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </body>
            </html>
            """, 
            user.getName(), 
            resultMessage, 
            closingMessage,
            job.getTitle(), 
            job.getCompanyName()
        );
    }
}
