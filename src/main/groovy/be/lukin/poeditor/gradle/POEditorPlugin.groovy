package be.lukin.poeditor.gradle

import be.lukin.poeditor.tasks.PullTask;
import be.lukin.poeditor.tasks.PushTask;
import be.lukin.poeditor.tasks.PushTermsTask;
import be.lukin.poeditor.tasks.InitTask;
import be.lukin.poeditor.tasks.StatusTask;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction;

class POEditorPlugin implements Plugin<Project> {
    public static final String GROUP = 'POEditor translations'
    
    @Override
    void apply(Project project) {
        //project.setGroup()
        project.setDescription("Manage translations from POEditor (http://poeditor.com)")

        // Define extension
        project.extensions.create("poeditor", POEditorExtension)
        
        // Define tasks
        project.task("poeditorInit", 
                type: InitTaskGradle,
                group: GROUP,
                description: "Initialization: create terms & languages")
        
        project.task("poeditorPush", 
                type: PushTaskGradle,
                group: GROUP,
                description: "Upload translations")
        
        project.task("poeditorPushTerms",
                type: PushTermsTaskGradle,
                group: GROUP, 
                description: "Upload terms")
        
        project.task("poeditorPull",
                type: PullTaskGradle,
                group: GROUP, 
                description: "Download translations")
        
        project.task("poeditorStatus",
                type: StatusTaskGradle,
                group: GROUP,
                description: "Existing project configuration in a more human readable format")
    }
}

class InitTaskGradle extends DefaultTask {
    @TaskAction
    def initialize() {
        def project = this.getProject();
        POEditorExtension extension = project.poeditor;
        new InitTask().configure(extension.toConfig()).handle();
    }
}

class PullTaskGradle extends DefaultTask {
    @TaskAction
    def initialize() {
        def project = this.getProject();
        POEditorExtension extension = project.poeditor;
        new PullTask().configure(extension.toConfig()).handle();
    }
}

class PushTaskGradle extends DefaultTask {
    /**
     * This task receives 3 optional arguments:
     *  - languages: a list of languages that you want to upload, default: all
     *  - override: force to override languages, default: false
     *  - noSleep: after each upload the task sleeps for 30 seconds. With this param you can disable it, default: false
     * 
     * Execute: gradle poeditorPush -Planguages=nl,fr -PnoSleep=true -Poverride=true
     * Execute: gradle poeditorPush --project-prop languages=nl,fr --project-prop noSleep=true
     */
    @TaskAction
    def initialize() {
        def project = this.getProject();
        def props = project.properties;
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("languages", (String) props.get("languages"));
        arguments.put("noSleep", (String) props.get("noSleep"));
        arguments.put("override", (String) props.get("override"));

        POEditorExtension extension = project.poeditor;
        def task = new PushTask();
        task.configure(extension.toConfig(), arguments);
        task.handle();
    }
}

class PushTermsTaskGradle extends DefaultTask {
    @TaskAction
    def initialize() {
        def project = this.getProject();
        POEditorExtension extension = project.poeditor;
        new PushTermsTask().configure(extension.toConfig()).handle();
    }
}

class StatusTaskGradle extends DefaultTask {
    @TaskAction
    def initialize() {
        def project = this.getProject();
        POEditorExtension extension = project.poeditor;
        new StatusTask().configure(extension.toConfig()).handle();
    }
}
