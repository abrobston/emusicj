; This is the installer for the classicsonline windows version
; It (like the rest of the classicsonline application) may be freely
; distributed under the terms of the GNU GPL.
; Copyright (C) 2007 Robin Sheat <robin@kallisti.net.nz>

;--------------------------------

!include "MUI.nsh"

; The name of the installer
Name "Reggae Country Download Manager"

; The file to write
OutFile "ReggaeCountry-installer.exe"

; The default installation directory
InstallDir $PROGRAMFILES\ReggaeCountry

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\ReggaeCountry_DLM" "Install_Dir"

;--------------------------------
; Interface Settings
!define MUI_ABORTWARNING

!define MUI_DIRECTORYPAGE_TEXT_TOP "Setup will install the Reggae Country Download Manager in the following folder. To install in a different folder, click Browse and select another folder. Click Install to start the installation. If you are not an administrator on this computer, make sure the folder selected is one you have permissions to save files in."

!define MUI_FINISHPAGE_RUN $INSTDIR\ReggaeCountry.exe
!define MUI_FINISHPAGE_LINK "Go to the Reggae Country website"
!define MUI_FINISHPAGE_LINK_LOCATION http://www.reggaecountry.com
!define MUI_FINISHPAGE_NOREBOOTSUPPORT

;--------------------------------

; Pages

  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
; Java Stuff
!define JRE_VERSION "1.5"
!define JRE_VERSION2 "1.6"
!define JRE_VERSION3 "1.7"
!define JRE_VERSION4 "1.8"
!define JRE_VERSION5 "1.9"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=86895&/jre-7u55-windows-i586.exe"

;--------------------------------
; The stuff to install
Section "Reggae Country Download Manager" SecCOL
  SectionIn RO
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File "ReggaeCountry.exe"
  File "COPYING"
  File "README-reggaecountry.txt"

  SetOutPath $INSTDIR\lib
  File "lib\*.*"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\ReggaeCountry_DLM "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "DisplayName" "Reggae Country DLM"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts" SecSMS

  CreateDirectory "$SMPROGRAMS\ReggaeCountry"
  CreateShortCut "$SMPROGRAMS\ReggaeCountry\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\ReggaeCountry\ReggaeCountry.lnk" "$INSTDIR\ReggaeCountry.exe" "" "$INSTDIR\ReggaeCountry.exe" 0
  
SectionEnd

Section "File Associations" SecFA

!define Index "Line${__LINE__}"
  ReadRegStr $1 HKCR ".rcm" ""
  StrCmp $1 "" "${Index}-NoBackup"
    StrCmp $1 "ReggaeCountryDLM.AlbumFile" "${Index}-NoBackup"
    WriteRegStr HKCR ".rcm" "backup_val" $1 
"${Index}-NoBackup:"
  WriteRegStr HKCR ".rcm" "" "ReggaeCountryDLM.AlbumFile"
  ReadRegStr $0 HKCR "ReggaeCountryDLM.AlbumFile" ""
  StrCmp $0 "" 0 "${Index}-Skip"
	WriteRegStr HKCR "ReggaeCountryDLM.AlbumFile" "" "Reggae Country Album File"
	WriteRegStr HKCR "ReggaeCountryDLM.AlbumFile\shell" "" "open"
	WriteRegStr HKCR "ReggaeCountryDLM.AlbumFile\DefaultIcon" "" "$INSTDIR\ReggaeCountry.exe,0" 
"${Index}-Skip:"
  WriteRegStr HKCR "ReggaeCountryDLM.AlbumFile\shell\open\command" "" \
    '$INSTDIR\ReggaeCountry.exe "%1"'
  WriteRegBin HKCR "ReggaeCountryDLM.AlbumFile" "EditFlags" 00000100
  System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)'
!undef Index

SectionEnd

Section "Install Java" SecIJ
    Call DetectJRE
SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecCOL ${LANG_ENGLISH} "Reggae Country Download Manager"
  LangString DESC_SecSMS ${LANG_ENGLISH} "Start Menu Shortcuts"
  LangString DESC_SecFA ${LANG_ENGLISH} "Associates .rcm files with this program"
  LangString DESC_SecIJ ${LANG_ENGLISH} "Checks to see if Java is installed and, if not, downloads and installs it"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecCOL} $(DESC_SecCOL)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecSMS} $(DESC_SecSMS)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecFA} $(DESC_SecFA)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecIJ} $(DESC_SecIJ)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END


;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove files and uninstaller
  Delete $INSTDIR\lib\*.*
  Delete $INSTDIR\ReggaeCountry.exe
  Delete $INSTDIR\COPYING
  Delete $INSTDIR\README-reggaecountry.txt
  Delete $INSTDIR\uninstall.exe
  RMDir "$INSTDIR\lib"
  RMDir "$INSTDIR"

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\ReggaeCountry\*.*"
  RMDir "$SMPROGRAMS\ReggaeCountry"
  
  ; Remove user files from .reggaecountry
  Delete "$PROFILE\.reggaecountry\*.*"
  RMDir "$PROFILE\.reggaecountry"

!define Index "Line${__LINE__}"
  ReadRegStr $1 HKCR ".rcm" ""
  StrCmp $1 "ReggaeCountryDLM.AlbumFile" 0 "${Index}-NoOwn" ; only do this if we own it
    ReadRegStr $1 HKCR ".rcm" "backup_val"
    StrCmp $1 "" 0 "${Index}-Restore" ; if backup="" then delete the whole key
      DeleteRegKey HKCR ".rcm"
    Goto "${Index}-NoOwn"
"${Index}-Restore:"
      WriteRegStr HKCR ".rcm" "" $1
      DeleteRegValue HKCR ".rcm" "backup_val"
   
    DeleteRegKey HKCR "ReggaeCountryDLM.AlbumFile" ;Delete key with association settings
 
    System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)'
"${Index}-NoOwn:"
!undef Index

  ; Remove the windows uninstaller key thing
  DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "DisplayName"
  DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "UninstallString"
  DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "NoModify"
  DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM" "NoRepair"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ReggaeCountry_DLM"

SectionEnd

;--------------------------------
; Functions
Function GetJRE
        MessageBox MB_OK "The Reggae Country Download Manager requires Java 1.5 or above, it will now \
                         be downloaded and installed"
 
        StrCpy $2 "$TEMP\Java Runtime Environment.exe"
        nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "Download failed, try going to java.com and installing it manually: $R0"
                Quit
        ExecWait $2
        Delete $2
FunctionEnd
 
 
Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} done
  StrCmp $2 ${JRE_VERSION2} done
  StrCmp $2 ${JRE_VERSION3} done
  StrCmp $2 ${JRE_VERSION4} done
  StrCmp $2 ${JRE_VERSION5} done
  
  Call GetJRE
  
  done:
FunctionEnd
