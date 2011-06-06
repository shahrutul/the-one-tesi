#java mainLaunch
#./one.sh -b 2 WDM_settings.txt m2mshare_settings.txt seedSettings.txt

#./one.sh -b 3 WDM_settings_pendolari.txt m2mshare_test_fileDivision.txt
#./one.sh -b 3 WDM_settings_pendolari.txt m2mshare_test_delega.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/100.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/150.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/200.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/250.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/300.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/350.txt
./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings.txt fileSettings/400.txt
svn add reports/100files/* reports/150files/* reports/200files/* reports/250files/* reports/300files/* reports/350files/* reports/400files/*
svn commit -m "nuovi dati con più files"

#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/100.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/150.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/200.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/250.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/300.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/350.txt
#./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt seedSettings2.txt fileSettings/400.txt
#svn add reports/100files/* reports/150files/* reports/200files/* reports/250files/* reports/300files/* reports/350files/* #reports/400files/*
#svn commit -m "altri nuovi dati con più files"
./runBatch2.sh

shutdown -h +1 "Spegnimento in 60 secondi" &
zenity --question --title 'Simulazioni finite!' --text 'Premi Ok per spegnere subito o No per interrompere lo spegnimento automatico'
if [[ $? == 0 ]] ; then
        echo "Spegnimento"
        shutdown -h now
else
        echo "NOOO Spegnimento"
        shutdown -c
fi

