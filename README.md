# Warehouse Management System
## Individual Project Java Course

This repository will contain the individual project for the [DCI Java Course](https://digitalcareerinstitute.org/), a fully functional prototype web application for a Warehouse Management System.

### Expected app features:
- a user system with roles and permissions for listing and placing orders
- maintaining the stock of the warehouse
- ordering items from the warehouse
- generating barcodes and labels
- reversing orders
- log actions from users
- reporting
- analyzing

### Implemented app features:
- list items by warehouse
- search items and place orders
- browsing by category
- log actions from users
- a user system with roles and permissions for placing orders

### Development setup:
- install [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/) with [Homebrew Package Manager](https://brew.sh/) in the Terminal:
  - `brew install --cask intellij-idea-ce`
- download and install [Amazon Corretto 11 JDK](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html) in IntelliJ IDEA by creating a new Java Project and selecting "Amazon Corretto 11" under Project SDK --> Download JDK
- after creating a new project, initialize it as a local git repository and connect it to the remote repository on Github.
  - `git init`
  - `git remote add origin [remote repository]`
- Make sure that the local repository has a main branch called "main" instead of "master", then update it from the remote repository.
  - `git checkout -b main`
  - `git pull origin main`
- create folder structure according to [Maven Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) for the local repository with the following shortcut in the Terminal:
  - `mkdir -p src/{main,test}/{java,resources}`
- create Java class Main.java inside main/java/
- add and commit changes and push them to the remote repository
  - `git add .`
  - `git commit -m "Create project structure"`
  - `git push origin main`

---

**License:**
[MIT License](LICENSE.txt)

**Author:** 
[Viktoria Karsten](https://github.com/vkarsten) 


