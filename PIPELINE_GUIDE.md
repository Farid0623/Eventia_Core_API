# 🔄 Guía de Pipelines CI/CD

## ¿Qué es un Pipeline?

Un **pipeline** es un proceso automatizado que:
1. Toma tu código
2. Lo compila
3. Ejecuta pruebas
4. Analiza calidad
5. (Opcionalmente) Lo despliega

---

## 📋 Pipeline Implementado: GitHub Actions

### Ubicación del Archivo

```
.github/workflows/ci-cd.yml
```

### ¿Cuándo se Ejecuta?

```yaml
on:
  push:
    branches: [ main, develop ]    # Al hacer push
  pull_request:
    branches: [ main, develop ]    # Al crear PR
```

### Pasos del Pipeline

```yaml
1. Checkout código
   ↓
2. Setup Java 21
   ↓
3. Cache de Gradle
   ↓
4. Instalar dependencias
   ↓
5. Ejecutar pruebas unitarias
   ↓
6. Ejecutar pruebas de integración
   ↓
7. Checkstyle (estilo de código)
   ↓
8. SpotBugs (análisis de seguridad)
   ↓
9. JaCoCo (cobertura)
   ↓
10. Upload reportes
    ↓
11. ✅ OK  o  ❌ FAILED
```

### Ver el Pipeline en Acción

1. Haz push a GitHub:
   ```bash
   git add .
   git commit -m "feat: mi cambio"
   git push origin main
   ```

2. Ve a GitHub → Tu repositorio → Pestaña "Actions"

3. Verás el pipeline ejecutándose en tiempo real

### Resultado Esperado

Si todo pasa:
```
================================================
                     OK                         
================================================
✅ All tests passed successfully!
✅ Security analysis completed!
✅ Code quality checks passed!
================================================
```

Si algo falla:
```
================================================
                   FAILED                       
================================================
❌ Pipeline failed. Please check the logs above.
================================================
```

---

## 🔧 Otras Opciones de Pipelines

### 1️⃣ GitLab CI/CD

**Archivo**: `.gitlab-ci.yml`

```yaml
stages:
  - build
  - test
  - quality
  - deploy

build:
  stage: build
  image: eclipse-temurin:21-jdk
  script:
    - ./gradlew clean build -x test

test:
  stage: test
  image: eclipse-temurin:21-jdk
  services:
    - postgres:17
    - redis:7
  script:
    - ./gradlew test

quality:
  stage: quality
  script:
    - ./gradlew checkstyleMain spotbugsMain

deploy:
  stage: deploy
  only:
    - main
  script:
    - docker build -t eventia-api .
    - docker push eventia-api
```

### 2️⃣ Jenkins

**Jenkinsfile**:

```groovy
pipeline {
    agent any
    
    tools {
        jdk 'JDK21'
        gradle 'Gradle8'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        
        stage('Quality Analysis') {
            steps {
                sh './gradlew checkstyleMain'
                sh './gradlew spotbugsMain'
            }
        }
        
        stage('Coverage') {
            steps {
                sh './gradlew jacocoTestReport'
                publishHTML([
                    reportDir: 'build/reports/jacoco/test/html',
                    reportFiles: 'index.html',
                    reportName: 'Code Coverage'
                ])
            }
        }
    }
    
    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```

### 3️⃣ CircleCI

**Archivo**: `.circleci/config.yml`

```yaml
version: 2.1

orbs:
  gradle: circleci/gradle@3.0

jobs:
  build_and_test:
    docker:
      - image: cimg/openjdk:21.0
      - image: postgres:17
        environment:
          POSTGRES_DB: eventia_test
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_pass
      - image: redis:7
    
    steps:
      - checkout
      
      - restore_cache:
          keys:
            - gradle-{{ checksum "build.gradle" }}
      
      - run:
          name: Build
          command: ./gradlew clean build -x test
      
      - run:
          name: Run Tests
          command: ./gradlew test
      
      - run:
          name: Code Quality
          command: |
            ./gradlew checkstyleMain
            ./gradlew spotbugsMain
      
      - save_cache:
          paths:
            - ~/.gradle
          key: gradle-{{ checksum "build.gradle" }}
      
      - store_test_results:
          path: build/test-results
      
      - store_artifacts:
          path: build/reports

workflows:
  build_test_deploy:
    jobs:
      - build_and_test
```

### 4️⃣ Azure Pipelines

**Archivo**: `azure-pipelines.yml`

```yaml
trigger:
  - main
  - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
  JAVA_VERSION: '21'

stages:
- stage: Build
  jobs:
  - job: BuildJob
    steps:
    - task: JavaToolInstaller@0
      inputs:
        versionSpec: '$(JAVA_VERSION)'
        jdkArchitectureOption: 'x64'
    
    - task: Gradle@2
      inputs:
        gradleWrapperFile: 'gradlew'
        tasks: 'clean build'
        options: '-x test'

- stage: Test
  jobs:
  - job: TestJob
    services:
      postgres:
        image: postgres:17
        ports:
          - 5432:5432
        env:
          POSTGRES_DB: eventia_test
      redis:
        image: redis:7
        ports:
          - 6379:6379
    
    steps:
    - task: Gradle@2
      inputs:
        tasks: 'test'
    
    - task: PublishTestResults@2
      inputs:
        testResultsFormat: 'JUnit'
        testResultsFiles: '**/build/test-results/test/*.xml'

- stage: Quality
  jobs:
  - job: QualityJob
    steps:
    - task: Gradle@2
      inputs:
        tasks: 'checkstyleMain spotbugsMain'
```

---

## 🚀 Pipeline Local (Sin CI/CD)

Si no quieres usar GitHub Actions, puedes ejecutar el pipeline localmente:

### Script: `run-pipeline.sh`

```bash
#!/bin/bash

echo "🔄 Ejecutando Pipeline Local"
echo "=============================="

# 1. Limpiar
echo "1️⃣  Limpiando..."
./gradlew clean

# 2. Compilar
echo "2️⃣  Compilando..."
./gradlew compileJava
if [ $? -ne 0 ]; then
    echo "❌ Compilación falló"
    exit 1
fi

# 3. Pruebas Unitarias
echo "3️⃣  Pruebas unitarias..."
./gradlew test --tests "*Test"
if [ $? -ne 0 ]; then
    echo "❌ Pruebas unitarias fallaron"
    exit 1
fi

# 4. Pruebas de Integración
echo "4️⃣  Pruebas de integración..."
./gradlew test --tests "*IntegrationTest"
if [ $? -ne 0 ]; then
    echo "❌ Pruebas de integración fallaron"
    exit 1
fi

# 5. Checkstyle
echo "5️⃣  Checkstyle..."
./gradlew checkstyleMain
if [ $? -ne 0 ]; then
    echo "⚠️  Checkstyle tiene warnings"
fi

# 6. SpotBugs
echo "6️⃣  SpotBugs..."
./gradlew spotbugsMain
if [ $? -ne 0 ]; then
    echo "⚠️  SpotBugs encontró problemas"
fi

# 7. Cobertura
echo "7️⃣  Cobertura..."
./gradlew jacocoTestReport

echo ""
echo "================================================"
echo "                     OK                         "
echo "================================================"
echo "✅ Pipeline completado exitosamente!"
echo ""
echo "📊 Ver reportes:"
echo "  Tests: open build/reports/tests/test/index.html"
echo "  Cobertura: open build/reports/jacoco/test/html/index.html"
echo "================================================"
```

### Ejecutar

```bash
chmod +x run-pipeline.sh
./run-pipeline.sh
```

---

## 📊 Integración con Servicios de Calidad

### SonarQube

```yaml
# En GitHub Actions
- name: SonarQube Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: |
    ./gradlew sonarqube \
      -Dsonar.projectKey=eventia-api \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.login=$SONAR_TOKEN
```

### Codecov (Cobertura)

```yaml
- name: Upload to Codecov
  uses: codecov/codecov-action@v4
  with:
    files: ./build/reports/jacoco/test/jacocoTestReport.xml
    fail_ci_if_error: false
```

### Dependabot (Dependencias)

**.github/dependabot.yml**:

```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 5
```

---

## ✅ Checklist de Pipeline

Antes de considerar el pipeline completo:

- [x] Compila el código
- [x] Ejecuta pruebas unitarias
- [x] Ejecuta pruebas de integración
- [x] Análisis de estilo (Checkstyle)
- [x] Análisis de seguridad (SpotBugs)
- [x] Genera reporte de cobertura
- [x] Falla si los tests fallan
- [x] Muestra mensaje claro (OK/FAILED)
- [x] Servicios de BD configurados (postgres, redis)
- [x] Cache para acelerar builds

---

## 🎯 Buenas Prácticas

### 1. Cache de Dependencias

```yaml
- name: Setup Gradle
  uses: gradle/gradle-build-action@v2
  with:
    cache-read-only: false
```

### 2. Matriz de Versiones

```yaml
strategy:
  matrix:
    java: [21, 22]
    os: [ubuntu-latest, macos-latest]
```

### 3. Paralelización

```yaml
jobs:
  test-unit:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew test --tests "*Test"
  
  test-integration:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew test --tests "*IntegrationTest"
```

### 4. Environments

```yaml
environment:
  name: production
  url: https://api.eventia.com
```

---

## 🚀 Comandos Rápidos

```bash
# Simular pipeline localmente
./run-pipeline.sh

# Ver estado en GitHub
# GitHub → Repositorio → Actions

# Ejecutar solo tests como en CI
./gradlew clean test

# Ejecutar análisis completo
./gradlew check
```

---

## 📚 Recursos

- **GitHub Actions**: https://docs.github.com/actions
- **GitLab CI/CD**: https://docs.gitlab.com/ee/ci/
- **Jenkins**: https://www.jenkins.io/doc/
- **CircleCI**: https://circleci.com/docs/
- **Azure Pipelines**: https://azure.microsoft.com/services/devops/pipelines/

---

**Tu pipeline está LISTO y configurado!** ✅

Cada push a GitHub ejecutará automáticamente el pipeline completo.

