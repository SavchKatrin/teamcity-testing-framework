package com.example.teamcity.api;

import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest{
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest(){
        step("Create user");
        step("Create project by user");
        step("Create buildType for project by user");
        step("Create buildType was created successfully with correct data");
    }

    @Test(description = "User should not be able to create two build types with same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoWithTheSameIdTypeTest(){
        step("Create user");
        step("Create project by user");
        step("Create buildType1 for project by user");
        step("Create buildType2 with the same id as buildType1 for project by user");
        step("Check buildType2 was not created with bad request code");
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Rules"})
    public void projectAdminCreateBuildTypeTest(){
        step("Create user");
        step("Create project");
        step("Grant user PROJECT_ADMIN role in project");
        step("Create buildType for project by user");
        step("Create buildType was created successfully");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Rules"})

    public void projectAdminCreateBuildTypeForAnotherBuildProjectTest(){
        step("Create user1");
        step("Create project1");
        step("Grant user1 PROJECT_ADMIN role in project1");

        step("Create user2");
        step("Create project2");
        step("Grant user2 PROJECT_ADMIN role in project2");

        step("Create buildType for project1 by user2");
        step("Check buildType was not created with forbidden code");
    }
}
