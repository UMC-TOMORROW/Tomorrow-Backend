package com.umc.tomorrow.global.infrastructure.mail;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.AttributedString;
import javax.imageio.ImageIO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ReceiptCardRenderer {

    private static final String TEMPLATE = "static/mail/tomorrowImage/receipt-card-base.png";
    private static final String FONT_BLD = "static/fonts/NotoSansKR-Bold.ttf";

    public ByteArrayResource render(String jobTitle, String companyName, String submittedAtIgnored) {
        try {
            ClassPathResource res = new ClassPathResource(TEMPLATE);
            if (!res.exists()) throw new IllegalStateException("Base image NOT FOUND: " + TEMPLATE);

            try (InputStream is = res.getInputStream()) {
                BufferedImage base = ImageIO.read(is);
                if (base == null) throw new IllegalStateException("ImageIO.read() returned null for: " + TEMPLATE);

                int W = base.getWidth(), H = base.getHeight();
                float scale = W / 595f; // 템플릿 기준 595

                // PNG 투명도 보존을 위해 ARGB 사용
                BufferedImage out = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = out.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                // 배경 투명으로 초기화
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, W, H);
                g.setComposite(AlphaComposite.SrcOver);

                // 베이스 PNG 위에 그리기
                g.drawImage(base, 0, 0, null);

                // 글자 크기 18
                Font bold = loadFont(FONT_BLD, 18f * scale);

                // ===== 값 칸 좌표 =====
                int leftMargin  = Math.round(90 * scale);
                int rightMargin = Math.round(70 * scale);
                int labelWidth  = Math.round(160 * scale);
                int gutter      = Math.round(12 * scale);
                int tableTop    = Math.round(372 * scale);
                int rowH        = Math.round(66 * scale);
                int vInset      = Math.round(10 * scale);

                int valueX = leftMargin + labelWidth + gutter;     // 두 줄 동일 X
                int valueW = (W - rightMargin) - valueX;

                // 지원공고명만 아래로 추가 이동
                int jobShiftDown = Math.round(22 * scale); // 필요 시 20~26 조정

                Rectangle jobRect =
                        new Rectangle(valueX,
                                tableTop + vInset + jobShiftDown,
                                valueW,
                                rowH - 2 * vInset);

                Rectangle companyRect =
                        new Rectangle(valueX,
                                tableTop + rowH + vInset,
                                valueW,
                                rowH - 2 * vInset);

                // 텍스트 그리기
                g.setColor(new Color(0x11, 0x33, 0x19));
                drawWrapped(g, jobTitle,    jobRect,    bold, 2, true);
                drawWrapped(g, companyName, companyRect, bold, 1, true);

                g.dispose();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // >>> PNG로 저장 <<<
                ImageIO.write(out, "png", bos);
                return new ByteArrayResource(bos.toByteArray());
            }
        } catch (Exception e) {
            throw new RuntimeException("접수 카드 이미지 생성 실패: " + e.getMessage(), e);
        }
    }

    private Font loadFont(String classpath, float size) {
        try (InputStream is = new ClassPathResource(classpath).getInputStream()) {
            Font f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f;
        } catch (Exception e) {
            return new Font("SansSerif", Font.PLAIN, Math.round(size));
        }
    }

    private void drawWrapped(Graphics2D g, String text, Rectangle area, Font font, int maxLines, boolean ellipsis) {
        // null 체크 강화 및 빈 문자열 처리
        if (text == null || text.trim().isEmpty()) {
            text = "정보 없음"; // 기본값 설정
        }
        
        g.setFont(font);

        // 빈 문자열이 아닌 경우에만 AttributedString 생성
        AttributedString att = new AttributedString(text);
        att.addAttribute(TextAttribute.FONT, font);
        LineBreakMeasurer lbm = new LineBreakMeasurer(att.getIterator(), g.getFontRenderContext());

        float x = area.x;
        float y = area.y + g.getFontMetrics().getAscent();
        float width = area.width;

        int lines = 0;
        while (lbm.getPosition() < text.length() && lines < maxLines) {
            int start = lbm.getPosition();
            int next = lbm.nextOffset(width);
            int limit = next;

            while (limit > start) {
                Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, start, limit, g);
                if (bounds.getWidth() <= width) break;
                limit--;
            }
            String line = text.substring(start, limit).trim();
            boolean hasMore = limit < text.length();

            if (hasMore && lines == maxLines - 1 && ellipsis) {
                String withDots = line + "…";
                while (g.getFontMetrics().stringWidth(withDots) > width && line.length() > 0) {
                    line = line.substring(0, line.length() - 1);
                    withDots = line + "…";
                }
                line = withDots;
                lbm.setPosition(text.length());
            } else {
                lbm.setPosition(limit);
            }

            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
            lines++;
        }
    }
}
