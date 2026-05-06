import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.install.InstallState
import jenkins.model.Jenkins

def instance = Jenkins.get()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
if (hudsonRealm.getUser('admin') == null) {
    hudsonRealm.createAccount('admin', 'admin123')
}
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

def pluginNames = [
    'configuration-as-code',
    'docker-workflow',
    'git',
    'gitlab-plugin',
    'kubernetes',
    'pipeline-stage-view',
    'workflow-aggregator'
]

def updateCenter = instance.getUpdateCenter()
updateCenter.updateAllSites()

pluginNames.findAll { pluginName ->
    instance.pluginManager.getPlugin(pluginName) == null
}.each { pluginName ->
    def plugin = updateCenter.getPlugin(pluginName)
    if (plugin != null) {
        plugin.deploy()
    }
}

instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)
instance.save()

