<link rel="stylesheet" href="https://stackedit.io/res-min/themes/base.css" />
<script type="text/javascript" src="https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_HTML"></script>


<!-- /!\ IMPORTANT /!\
*Note:* Considers to open this file with StackEdit.io (https://stackedit.io)
1. Open the menu panel with <i class="icon-provider-stackedit"></i>
2. Select <i class="icon-hdd"></i> **Import from disk**
3. Browse or Drop this file (`.md`)
4. Edit as your wishes
5. Use <i class="icon-hdd"></i> **Export to disk**
    - Select <i class="icon-download"></i> **Using template** to export as HTML viewer
    - Select <i class="icon-download"></i> **As Markdown** to export as Mardown file
-->

**Pomona**
==========

***Yammer User Management Script***
----------------------------------------

----------

[TOC]

----------

### <i class="icon-help-circled"></i> *What is it?*
The `Yammer User Management` script enables to manage Yammer users via the REST API.
It allows `creating`, `suspending`, `listing` and `updating` the Yammer users via a CSV file which holds the list of users email (and potentially other informations) to use.


### <i class="icon-cog"></i> *Configuration*
This script just need a Powershell console (4.0+) for full compatibility.


### <i class="icon-hdd"></i> *Installation*
No installation procedure.
Run the `YammerUserManagement.ps1` script with or without the defined command line arguments.


### <i class="icon-terminal"></i> *Operating Instructions*
The script `Yammer User Management` has many arguments setting its behavior :
Name | Value(s) | Optional | Description
:----|:---------|:--------:|:------------
GetToken | | True | Run the external script `Yammer Get Token` to get the access token. It need your client id, your client secret and the redirect URL.
YammerToken | String | True | Set the Yammer access token. It supplants the call to GetToken if given.
CSVFile | Path | False | Set the path to the CSV file which hold the users email
EWall | | True | Display more informations about requests, such as expected errors or warnings
Method | Get,Disable,Update,Create | False | Set the method to use :<br/>- `Get` will collect all Yammer active users.<br/>- `Disable` will suspend every users contained in the CSV file and in the Yammer users database.<br/>- `Update` will pick up all informations given in the CSV file and update the users, if they exists, with it.<br/>- `Create` will add given users to the Yammer network.

> **Note:** Mandatory arguments could be not passed by command line, but then will be prompt to the user.

If `GetToken` is set, then the script `YammerGetToken.ps1` will be executed. Here are the command line arguments for this script :
Name | Value(s) | Optional | Description
:----|:---------|:--------:|:------------
ClientID | String | False | The client id given by the Yammer application
ClientSecret | String | False | The client secret given by the Yammer application
RedirectURI | URI | False | The redirect URI given by the Yammer application

> **Note:** Such as the `Yammer User Management` script, mandatory arguments could be set at the beginning of the running script. In case of a call from the `Yammer User Management` script, arguments can not be passed by command line, and will be prompt to the user.


### <i class="icon-road"></i> *Workflow*
```flow
start=>start: Start
args=>subroutine: Parse args
main=>operation: Main
gt=>condition: GetToken ?
rygt=>subroutine: Run YammerGetToken.ps1
rht=>subroutine: Prompt for token
gcsv=>operation: Get CSV table
gycu=>operation: Get Yammer current user
gyu=>operation: Get Yammer users
sm=>subroutine: Method ?
smd=>condition: Disable ?
smu=>condition: Update ?
smc=>condition: Create ?
smg=>condition: Get ?
smdefault=>subroutine: Default
dyu=>operation: Disable Yammer users
uyu=>operation: Update Yammer users
cyu=>operation: Create Yammer users
syu=>operation: Show Yammer users
end=>end: End

start->args->main->gt
gt(yes, right)->rygt->gcsv
gt(no)->rht->gcsv->gycu->gyu->sm->smd
smd(yes)->dyu->end
smd(no, right)->smu
smu(yes)->uyu->end
smu(no, right)->smc
smc(yes)->cyu->end
smc(no, right)->smg
smg(yes)->syu
smg(no)->smdefault->end
syu->end
```


### <i class="icon-file"></i> *File Manifest*
> Pomona.zip
> |--- YammerUserManagement.ps1
> |--- YammerGetToken.ps1
> |--- users.csv
> |--- Pomona - Yammer User Management - Readme.md
> |--- Pomona - Yammer User Management - Readme.html

The `users.csv` file is an example. You can use your own CSV file.
The `HTML` readme is the user-friendly view.
The `Markdown` readme is for editing usage.
> *Note:* Considers to open this file with [StackEdit.io](https://stackedit.io)
> 
> 1. Open the menu panel with <i class="icon-provider-stackedit"></i>
> 2. Select <i class="icon-hdd"></i> **Import from disk**
> 3. Browse or Drop this file (`.md`)
> 4. Edit as your wishes
> 5. Use <i class="icon-hdd"></i> **Export to disk**
    - Select <i class="icon-download"></i> **Using template** to export as HTML viewer
    - Select <i class="icon-download"></i> **As Markdown** to export as Mardown file
>

### <i class="icon-bug"></i> *Known Bugs*
Following points do not constitute "true" bugs, but rather constraints :

- Created users are not activated, state is pending. The users have to log in to activate.
- Updating users from CSV file requires a syntax specific format : each field (i.e. csv header, first line) has to be strictly identical to Yammer user's fields (e.g. full_name : OK fullname : NOK)


### <i class="icon-wrench"></i> *Troubleshooting*

### <i class="icon-users"></i> *Credits and Acknowledgements*

### <i class="icon-info-circled"></i> *Changelog*

### <i class="icon-user"></i> *Contact*
[Ramzi Ghribi](mailto:ramzi.ghribi@vnext.fr)
[Jeremy Bidet](mailto:jeremy.bidet@vnext.fr)


### <i class="icon-shield"></i> *Copyright And Licensing Information*
&copy; Copyright vNext


----------

