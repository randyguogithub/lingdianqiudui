import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.io.BufferedWriter
import java.io.OutputStreamWriter

def create_release (user, repo, token, branch_from, new_branch){
    // Main function (first)

    def githubAPIurl = "https://api.github.com/repos/${user}/${repo}"
    def githubAuthString = "${token}".getBytes().encodeBase64().toString()

    def sha = get_sha (githubAPIurl, githubAuthString, branch_from)
    def body = [ref: "refs/heads/${new_branch}", sha: sha]

    // Check if branch already exist and create new if not
    def connection = new URL("${githubAPIurl}/git/refs/heads/${new_branch}").openConnection()
    connection.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
    connection.requestMethod = 'GET'
    if (connection.responseCode == 200) {
        println "Branch ${new_branch} already exist in ${repo} repo."
        merge (githubAPIurl, githubAuthString, repo, new_branch, branch_from)
    } else {
        println 'create new branch'
        def connection3 = new URL("${githubAPIurl}/git/refs").openConnection()
        connection3.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
        connection3.setRequestMethod("POST")
        connection3.setDoOutput(true)

        def httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(connection3.getOutputStream()))
        httpRequestBodyWriter.write(JsonOutput.toJson(body))
        httpRequestBodyWriter.close()

        if (connection3.responseCode == 201) {
            println "New branch ${new_branch} successfully created in ${repo} repo from ${branch_from} branch"
        } else {
            println 'An error occurred: ' +
                    connection3.responseCode + ' ' +
                    connection3.responseMessage
        }
    }
}

def delivery_release (user, repo, token, branch_from, branch_into){
    // Main function (second)

    def githubAPIurl = "https://api.github.com/repos/${user}/${repo}"
    def githubAuthString = "${token}".getBytes().encodeBase64().toString()

    // Check if branches exist
    branches = [branch_from, branch_into]
    branches.each { branch ->
      def connection = new URL("${githubAPIurl}/git/refs/heads/${branch}").openConnection()
      connection.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
      connection.requestMethod = 'GET'
      if (connection.responseCode == 200) {
        println "Branch ${branch} exist in ${repo} repo."
      } 
      else { currentBuild.result = 'FAILURE'; error("Branch ${branch} haven't found in ${repo} repo.") }
    }
    merge(githubAPIurl, githubAuthString, repo, branch_into, branch_from)
}    

def get_sha (githubAPIurl, githubAuthString, branch_from){
   
    // Get commit sha
    def connection = new URL("${githubAPIurl}/git/refs/heads/${branch_from}").openConnection()
    connection.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
    connection.requestMethod = 'GET'
    if (connection.responseCode == 200) {
        request_json = connection.content.text
    } else {
        println 'An error occurred: ' +
                connection.responseCode + ' ' +
                connection.responseMessage
    }
    def parse_json = new JsonSlurper().parseText(request_json)
    def sha = parse_json.object.sha
    return sha
} 

def delete_reference (user, repo, token, branch) {
    // Remove branch
    def githubAPIurl = "https://api.github.com/repos/${user}/${repo}"
    def githubAuthString = "${token}".getBytes().encodeBase64().toString()

    def connection = new URL("${githubAPIurl}/git/refs/heads/${branch}").openConnection()
    connection.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
    connection.requestMethod = 'DELETE'
    if (connection.responseCode == 204) {
        println "Branch ${branch} have removed from ${repo} repo."
    } else {
        println 'An error occurred: ' +
                connection.responseCode + ' ' +
                connection.responseMessage
    }
}

def merge (githubAPIurl, githubAuthString, repo, base_branch, target_branch) {
    /* 
       base_branch - name of the base branch that the target_branch will be merged into.
       target_branch -  name of the branch to merge
    */
    def body = [base: base_branch, head: target_branch]

    println "Merging ${target_branch} into ${base_branch} ..."
        def connection = new URL("${githubAPIurl}/merges").openConnection()
        connection.setRequestProperty( "Authorization", "Basic ${githubAuthString}" )
        connection.setRequestMethod("POST")
        connection.setDoOutput(true)

        def httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))
        httpRequestBodyWriter.write(JsonOutput.toJson(body))
        httpRequestBodyWriter.close()

        if (connection.responseCode == 201) { println "${target_branch} was successfully merge into ${base_branch}" }
        else if (connection.responseCode == 204) { println "Nothing to merge" }
        else if (connection.responseCode == 409) { currentBuild.result = 'FAILURE'; error("Merge conflict in ${repo} repo.") }
        else {
            println 'An error occurred: ' +
                    connection.responseCode + ' ' +
                    connection.responseMessage
        }

}

def create_tag (user, repo, token, branch, tag_name, release_name){

    def postUrl = "https://api.github.com/repos/${user}/${repo}/releases"
    def authString = "${token}".getBytes().encodeBase64().toString()

    def body = [tag_name: tag_name, target_commitish: branch, name: release_name]

    // Create new release tag
    def connection = new URL(postUrl).openConnection()
    connection.setRequestProperty( "Authorization", "Basic ${authString}" )
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)

    def httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))
    httpRequestBodyWriter.write(JsonOutput.toJson(body))
    httpRequestBodyWriter.close()

    if (connection.responseCode == 201) {
        println "New tag ${tag_name} was saccessfully created in ${repo} repo."
    } else {
        println 'An error occurred: ' +
                connection.responseCode + ' ' +
                connection.responseMessage
    }
}

def get_branch_list (user, repo, token) {
    def getUrl = "https://api.github.com/repos/${user}/${repo}/branches"
    def authString = "${token}".getBytes().encodeBase64().toString()

    def connection = new URL(getUrl).openConnection()
    connection.setRequestProperty( "Authorization", "Basic ${authString}" )
    connection.requestMethod = 'GET'
    if (connection.responseCode == 200) {
      request_json = connection.content.text
    } else {
        println 'An error occurred: ' +
                connection.responseCode + ' ' +
                connection.responseMessage
    }

    def parse_json = new JsonSlurper().parseText(request_json)
    branch_list = []
    parse_json.each { branch -> branch_list.push(branch.name) }

    return branch_list    
}

return this
