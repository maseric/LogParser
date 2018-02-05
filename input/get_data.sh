#!/usr/bin/sh

#variables
machine=$(uname -n)
horo=$(date '+%Y%m%d')

#recherche h deb et fin dans archivelog
zgrep -h k01_repartitionChargesAR /archivelog/feilog.* |grep -e X121 -e X122 > k01_repartitionChargesAR.data.$machine.$horo
#recherche h deb et fin dans FE_LOG
grep k01_repartitionChargesAR $FE_LOG |grep -e X121 -e X122 >> k01_repartitionChargesAR.data.$machine.$horo


#recherche compteurs dans archivelog
zgrep -h k01_repartitionChargesAR /archivelog/feilog.* |grep X333 > k01_repartitionChargesAR.cpt.$machine.$horo
#recherche compteurs dans FE_LOG
grep k01_repartitionChargesAR $FE_LOG |grep X333 >> k01_repartitionChargesAR.cpt.$machine.$horo

