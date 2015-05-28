#!/usr/bin/perl

use strict;
use warnings;
use Cwd;

my $dir = "/Users/Crystal/Projects/Bookmarker/Bookmarker/src/com/purplecat/bookmarker/";
my $inFile = $dir . "resources/data/strings_labels.xml";
my $outFile = $dir . "Resources.java";
my $package = "com.purplecat.bookmarker";

open IN, "<:encoding(UTF-8)", $inFile or warn "invalid IN file $inFile";
open OUT, ">:encoding(UTF-8)", $outFile or warn "invalid OUT file $outFile";

print "Starting program...\n";

print OUT "package $package;\n";
print OUT "\n";
print OUT "public class Resources {\n";

#Printing Resource strings
print "  printing resources strings...\n";
print OUT "\tpublic static class string {\n";

my $currId = 0x7f000000;

while (<IN>) {
	chomp;
	my $line = $_;
	if ( $line =~ m/<(\w+) name="(\w+)">([^<]+)</ ) {
		my $tagName = $1;
		my $idName = $2;
		my $stringValue = $3;
		
		if ( $tagName eq "string" ) {
			print OUT sprintf("\t\tpublic static final int %s = 0x%x;\n", $idName, $currId);
		}
		$currId++;
	}
}

print OUT "\t}\n";
print OUT "}\n";
print "Program finished.\n";