In `my previous post <https://baptiste-wicht.com/posts/2017/06/jenkins-declarative-pipeline-and-awesome-github-integration.html>`_, I presented a few news about Jenkins and about the fact that I switched to declarative pipelines and Github Organization support for my projects.

The main issue I had with this system is that I lost the ability to get
notifications on build that recover. Normally, I would get an email indicating
that build X was back to normal, but I haven't found a way to solve that for
declarative pipeline.

By following a few posts on StackOverflow, I now have the solution and it is the
same problem that was already present in scripted pipelines. Namely, the status
of the current build is not set early enough for the notification.  Basically,
you have to set the notification yourself. Here is what a declarative pipeline
looks like:

.. code:: groovy

    pipeline {
        agent any

        stages {

            // Normal Stages

            stage ('success'){
                steps {
                    script {
                        currentBuild.result = 'SUCCESS'
                    }
                }
            }
        }

        post {
            failure {
                script {
                    currentBuild.result = 'FAILURE'
                }
            }

            always {
                step([$class: 'Mailer',
                    notifyEveryUnstableBuild: true,
                    recipients: "baptiste.wicht@gmail.com",
                    sendToIndividuals: true])
            }
        }
    }

There are two important things here. First, a new stage (success) is added that
simply set the result of the current build to SUCCESS once it is done. It must
be the last stage on the pipeline. This could also be added as the last step of
the last stage instead of adding a new stage, but I think it's clearer like
this. The second thing is the failure block in which the result of the current
build is set to FAILURE. With these two things, the Mailer plugin now sends
notification when a build has been fixed.

I hope that will help some of you. I personally think that it should be much
easier than that. All this boilerplate is polluting the pipeline that should be
kept more maintainable, but for now it seems, it's the nicest way to achieve
that, short of handling all conditions in the post block and sending mail
directly there, but that would result in even more boilerplate code.
