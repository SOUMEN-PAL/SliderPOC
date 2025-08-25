package org.zynetic.sliderpoc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform