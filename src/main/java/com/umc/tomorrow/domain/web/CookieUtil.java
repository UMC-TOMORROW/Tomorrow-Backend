package com.umc.tomorrow.domain.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {

    private CookieUtil() {}

    public static boolean isLocal(HttpServletRequest req) {
        if (req == null) return false;
        String host = req.getServerName();
        return host == null
                || "localhost".equalsIgnoreCase(host)
                || host.startsWith("127.0.0.1");
    }

    public static void addHttpOnlyTokenCookie(HttpServletResponse res,
                                              HttpServletRequest req, // 로컬 판별용
                                              String name,
                                              String value,
                                              int maxAgeSeconds) {
        boolean local = isLocal(req);
        // 로컬(HTTP): SameSite=Lax, Secure 없음
        // 운영(HTTPS): SameSite=None; Secure
        String header = local
                ? String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                name, value, maxAgeSeconds)
                : String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                name, value, maxAgeSeconds);
        res.addHeader("Set-Cookie", header);
    }

    public static void removeCookie(HttpServletResponse res,
                                    HttpServletRequest req,
                                    String name) {
        boolean local = isLocal(req);
        String header = local
                ? String.format("%s=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax", name)
                : String.format("%s=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None", name);
        res.addHeader("Set-Cookie", header);
    }
}
