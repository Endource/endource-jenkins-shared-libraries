#!/usr/bin/groovy
package com.endource

def runBuild(name, specFile, artifactDir, envParams='') {
    script {
        def buildSpec = readFile(specFile)
        def result = awsCodeBuild(
            projectName: name,
            awsAccessKey: "${env.AWS_ACCESS_KEY_ID}", awsSecretKey: "${env.AWS_SECRET_ACCESS_KEY}", credentialsType: 'keys',region: "${env.AWS_DEFAULT_REGION}",
            artifactLocationOverride: 'endource-codebuild', artifactNameOverride: 'reports.zip', artifactNamespaceOverride: 'BUILD_ID', artifactPackagingOverride: 'ZIP', artifactPathOverride: name, artifactTypeOverride: 'S3',
            buildSpecFile: buildSpec,
            envVariables: envParams,
            sourceControlType: 'project', sourceVersion: "${GIT_COMMIT}", sseAlgorithm: 'AES256')

        sh "aws s3 cp s3://${result.getArtifactsLocation().split(':::')[1]} ${artifactDir}/reports.zip || true"
        sh "unzip -d ${artifactDir} ${artifactDir}/reports.zip"
    }
}

return this
