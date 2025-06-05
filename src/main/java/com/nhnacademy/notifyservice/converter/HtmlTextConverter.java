package com.nhnacademy.notifyservice.converter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class HtmlTextConverter {

    /**
     * 관리자용 알림 메시지로 HTML을 변환 (구조화된 텍스트)
     */
    public String convertToAdminNotification(String htmlContent) {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            return "";
        }

        Document doc = Jsoup.parse(htmlContent);
        StringBuilder result = new StringBuilder();

        // 사용자 이메일 추출
        String userEmail = extractUserEmail(doc);

        // 제목 추출 및 사용자 정보와 함께 표시
        Element titleElement = doc.selectFirst("h1, h2, h3, h4, h5, h6");
        if (titleElement != null && !userEmail.isEmpty()) {
            result.append("【").append(userEmail).append("】님이 ")
                    .append(titleElement.text()).append("\n\n");
        } else if (titleElement != null) {
            result.append("【알림】 ").append(titleElement.text()).append("\n");
        }

        // 핵심 정보 추출 (예약번호, 회의실, 날짜 등)
        String keyInfo = extractKeyInfoFormatted(doc);
        if (!keyInfo.isEmpty()) {
            result.append(keyInfo).append("\n\n");
        }

        // CTA 버튼 처리 - 관리자 페이지로 링크 변경
        Elements ctaElements = doc.select("div.cta, .cta");
        for (Element cta : ctaElements) {
            Elements links = cta.select("a");
            for (Element link : links) {
                // 원본 링크는 무시하고 관리자 페이지로 변경
                String customLinkText = "예약 내역 보기";
                String adminUrl = "/admin/booking"; // 관리자 페이지 URL

                // HTML 하이퍼링크 형태로 변경
                result.append("🔗 <a href=\"").append(adminUrl)
                        .append("\" target=\"_blank\" style=\"color: #007bff; text-decoration: underline;\">")
                        .append(customLinkText).append("</a>");
            }
        }

        return result.toString().trim();
    }

    /**
     * 관리자용 요약 메시지 생성 (팝업용)
     */
    public String createAdminSummary(String htmlContent, int maxLength) {
        Document doc = Jsoup.parse(htmlContent);

        // 사용자 이메일 추출
        String userEmail = extractUserEmail(doc);

        // 제목에서 핵심 동작 추출
        Element titleElement = doc.selectFirst("h1, h2, h3, h4, h5, h6");
        String action = "";
        if (titleElement != null) {
            String title = titleElement.text();
            if (title.contains("예약이 완료")) {
                action = "회의실을 예약하였습니다";
            } else if (title.contains("예약이 취소")) {
                action = "회의실 예약을 취소하였습니다";
            } else if (title.contains("예약이 변경")) {
                action = "회의실 예약을 변경하였습니다";
            } else {
                action = title;
            }
        }

        String summary = userEmail.isEmpty() ? action : userEmail + "님이 " + action;

        if (summary.length() <= maxLength) {
            return summary;
        }

        return summary.substring(0, maxLength - 3) + "...";
    }

    /**
     * HTML에서 사용자 이메일 추출
     */
    private String extractUserEmail(Document doc) {
        // strong 태그에서 이메일 패턴 찾기
        Elements strongElements = doc.select("strong");
        for (Element strong : strongElements) {
            String text = strong.text();
            if (text.contains("@") && text.contains(".")) {
                return text;
            }
        }

        // p 태그에서 이메일 패턴 찾기
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            String text = p.text();
            if (text.contains("@") && text.contains("님")) {
                // "asdf@test.com님" 형태에서 이메일 추출
                String[] parts = text.split("님");
                if (parts.length > 0) {
                    String emailPart = parts[0].trim();
                    if (emailPart.contains("@")) {
                        return emailPart;
                    }
                }
            }
        }

        return "";
    }

    /**
     * HTML에서 핵심 정보를 포맷된 형태로 추출 (각각 새 줄에)
     */
    private String extractKeyInfoFormatted(Document doc) {
        StringBuilder keyInfo = new StringBuilder();

        // strong 태그로 강조된 정보들 추출
        Elements strongElements = doc.select("strong");
        for (Element strong : strongElements) {
            String text = strong.text();

            // 이메일은 제외
            if (text.contains("@")) {
                continue;
            }

            String nextText = "";
            if (strong.nextSibling() != null) {
                nextText = strong.nextSibling().toString().trim();
                if (nextText.startsWith(":")) {
                    nextText = nextText.substring(1).trim();
                }
                // br 태그 이후의 텍스트도 확인
                if (nextText.isEmpty() && strong.parent() != null) {
                    String parentHtml = strong.parent().html();
                    String[] parts = parentHtml.split("<strong>" + text + "</strong>");
                    if (parts.length > 1) {
                        nextText = parts[1].replaceAll("<[^>]*>", "").trim();
                        if (nextText.startsWith(":")) {
                            nextText = nextText.substring(1).trim();
                        }
                    }
                }
            }

            if (text.contains("예약 번호") || text.contains("회의실") || text.contains("날짜")) {
                // 각 정보를 새 줄에 표시
                keyInfo.append("【").append(text).append("】 ").append(nextText).append("\n");
            }
        }

        return keyInfo.toString().trim();
    }

    /**
     * 순수 텍스트 변환 (기존 기능 유지)
     */
    public String convertToPlainText(String htmlContent) {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            return "";
        }
        return Jsoup.parse(htmlContent).text();
    }
}
