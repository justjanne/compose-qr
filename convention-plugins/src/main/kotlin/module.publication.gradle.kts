import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })

        // Provide artifacts information required by Maven Central
        pom {
            name.set("Compose QR")
            description.set("Kotlin QR Code library")
            url.set("https://github.com/justjanne/compose-qr")

            licenses {
                license {
                    name.set("MPL-2.0")
                    url.set("https://opensource.org/license/mpl-2-0")
                }
            }
            developers {
                developer {
                    id.set("justjanne")
                    name.set("Janne Mareike Koschinski")
                }
            }
            scm {
                url.set("https://github.com/justjanne/compose-qr")
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
