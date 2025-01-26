import org.gradle.kotlin.dsl.extra
import java.util.Properties

val azureProps = Properties()
val azureFile = file("${rootDir}/azure.properties")

if (azureFile.exists()) {
    azureProps.load(azureFile.inputStream())
}
val mavenAccessToken: String by project
val token = System.getenv("AZURE_PACKAGES_ACCESSTOKEN")
    ?: azureProps.getProperty("azure.packages.accessToken")
    ?: System.getenv("SYSTEM_ACCESSTOKEN")
    ?: azureProps.getProperty("system.accessToken")
    ?: System.getenv("ARTIFACT_ACCESSTOKEN")
    ?: azureProps.getProperty("artifact.accessToken")
extra["mavenAccessToken"] = token

subprojects {
    repositories {
        maven {
            url = uri("https://pkgs.dev.azure.com/foodtruckinc/Wonder/_packaging/maven-local/maven/v1")
            name = "maven-local"
            credentials {
                username = "foodtruckinc"
                password = mavenAccessToken
            }
        }
    }
}
