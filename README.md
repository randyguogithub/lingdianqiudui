# lingdianqiudui
零点球队website



- name: Clone hello-world-war repo
      git:
        repo: https://github.com/dwillington/hello-world-war.git
        dest: /tmp/hello-world-war
    #https://webcache.googleusercontent.com/search?q=cache:W8wvMriZ-xAJ:https://rickosborne.org/blog/2019/01/extract-a-maven-module-version-with-xpath-and-ansible/+&cd=7&hl=en&ct=clnk&gl=ca
    - name: get pom version
      # command: pom{{ item }}=$(xmllint --xpath "/*[namespace-uri()='http://maven.apache.org/POM/4.0.0' and local-name()='project']/*[namespace-uri()='http://maven.apache.org/POM/4.0.0' and local-name()='{{ item }}']/text()" /tmp/hello-world-war/pom.xml)
      command: xmllint --xpath "/*[namespace-uri()='http://maven.apache.org/POM/4.0.0' and local-name()='project']/*[namespace-uri()='http://maven.apache.org/POM/4.0.0' and local-name()='version']/text()" /tmp/hello-world-war/pom.xml
      register: pomVersion
    - debug:
        var: pomVersion.stdout
