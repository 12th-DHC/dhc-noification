package com.sungjujjang.dhc.notification.domain.email;

public record EmailQueueDTO(
        String email,
        String name,
        int room,
        boolean passed,
        String reason
) {
}
