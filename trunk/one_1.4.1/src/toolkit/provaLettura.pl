#! /usr/bin/perl

# Counts averages from multiple MessageStats reports

package Toolkit;
use strict;
use warnings;
use Getopt::Long;
use FileHandle;

# path to the gnuplot program
my $gnuplot = "gnuplot";
my (
	$outfile, $constTotalSum, $hitCountIndex,
	$labelRe, $term,          $range,
	$params, $comp, $logscale, $help
);
$hitCountIndex = 1               unless defined $hitCountIndex;
$term          = "emf"           unless defined $term;
$params        = "smooth unique" unless defined $params;
$labelRe       = '([^_]*)_'      unless defined $labelRe;

my $plotfile = "prova.gnuplot";



my $error;
my $help;

my $usage = '
usage: [-error] [-help] <input file names>
';

GetOptions("error!" => \$error, "help|?!" => \$help);

if (not $help and not @ARGV) {
    print "Missing required parameter(s)\n";
    print $usage;
    exit();
}

if ($help) {
    print 'MessageStats report file value averager. Prints out average for 
numeric key-value pairs. 
Expected syntax for input files: <key>: <value>
Output syntax: <key> <average> [<min> <max>]';
    print "\n$usage";
    print '
options:
   
error  Add minimum and maximum values to the output (for error bars)

';
    exit();
}



my $fileCount = @ARGV;

my @fileHandles0;
my @fileHandles1;
my @fileHandles2;

# open all input files
#print "# Average over $fileCount files: @ARGV\n";

my $i0 = 0;
my $i1 = 0;
my $i2 = 0;
for (my $i=0; $i < $fileCount; $i++) {
  my $inFile = $ARGV[$i];
  my $fh = new FileHandle;
  $fh->open("<$inFile") or die "Can't open $inFile: $!";
  if($inFile =~ m/Del0/){
	$fileHandles0[$i0] = $fh;
	$i0++;
  }
  if($inFile =~ m/Del1_FDS1/){
	$fileHandles1[$i1] = $fh;
	$i1++;
  }
  if($inFile =~ m/Del2/){
	$fileHandles2[$i2] = $fh;
	$i2++;
  }
}

my $count0 = @fileHandles0;
my $count1 = @fileHandles1;
my $count2 = @fileHandles2;


print "$fileCount files di input: 
$count0 con delega a 0
$count1 con delega a 1
$count2 con delega a 2
";

my $cont = 1;
my $sum = 0;
while($cont) {
  
  my ($key, $value);
  my $min = undef;
  my $max = undef;
  my $oldKey = undef;
  
  # read one line from each file and count average
  for (my $i=0; $i < $count0; $i++) {
    my $fh = $fileHandles0[$i];
    $_ = <$fh>;
    if (not $_) { # no more input
      $cont = 0; 
      last;
    }
   # ($key, $value) = m/(\S+):\t([\d\.]+)/;
     ($key, $value) = m/([\w\s]+):\t([\d\.]+)/;
	if (not defined $key or not defined $value) {		
		next; # not numeric value 
	}

    if($key ne 'Simulation time'){
		next; # not riguardo al tempo
	}
    $sum += $value;
        
	if (defined $oldKey and $oldKey ne $key) {
		die "key mismatch: $key vs. $oldKey";
	}
	$oldKey = $key;
	
    # update min and max values
    if (not defined $min) { # first value for the round
      $min = $value;
      $max = $value;
    }
    if ($value > $max) {
      $max = $value;
    }
    if ($value < $min) {
      $min = $value;
    }
  }

   
}

my $sumOre = $sum / 3600;
   print "tempo totale di simulazione: $sum secondi ($sumOre ore)
";

open( PLOT, ">$plotfile" ) or die "Can't open plot output file $plotfile : $!";
print PLOT "plot ";
print PLOT "10, 15";
print PLOT " title \"Titolo1\"";

print PLOT " $params";


print PLOT "\n";
close(PLOT);

if ( not $term eq "na" ) {
	system("$gnuplot $plotfile") == 0 or die "Can't run gnuplot: $?";
}

