#java mainLaunch
#./one.sh -b 2 WDM_settings.txt m2mshare_settings.txt seedSettings.txt

#./one.sh -b 3 WDM_settings_pendolari.txt m2mshare_test_fileDivision.txt
#./one.sh -b 3 WDM_settings_pendolari.txt m2mshare_test_delega.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings_temp.txt seedSettings.txt
svn commit -m "simulazione 372156336"
./one.sh -b 3 WDM_settings.txt m2mshare_settings_temp.txt seedSettings2.txt
svn commit -m "simulazione 2030952567"

shutdown -h +1 "Spegnimento in 60 secondi" &
zenity --question --title 'Simulazioni finite!' --text 'Premi Ok per spegnere subito o No per interrompere lo spegnimento automatico'
if [[ $? == 0 ]] ; then
        echo "Spegnimento"
        shutdown -h now
else
        echo "NOOO Spegnimento"
        shutdown -c
fi

