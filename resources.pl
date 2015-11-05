#!/usr/bin/perl

use strict;
use warnings;
use Cwd;

sub parseResourceFile($$);

my $packageName = "bookmarker";

my $currDir = cwd();
my $dir = $currDir . "/src/main/java/com/purplecat/$packageName/";
my $outFile = $dir . "Resources.java";
my $package = "com.purplecat.$packageName";

open OUT, ">:encoding(UTF-8)", $outFile or warn "invalid OUT file $outFile";

print "Starting program...\n";

print OUT "package $package;\n";
print OUT "\n";
print OUT "public class Resources {\n";

#Printing Resource strings

my $currId = 0x7f000000;
parseResourceFile("strings_labels.xml", "string");
parseResourceFile("images.xml", "image");

print OUT "}\n";
print "Program finished.\n";

sub parseResourceFile($$) {
	my ($fileName,$expectedTag) = @_;
	
	print "  printing resources $expectedTag items...\n";
	print OUT "\tpublic static class $expectedTag {\n";

	my $inFile = $dir . "resources/data/" . $fileName;

	open IN, "<:encoding(UTF-8)", $inFile or warn "invalid IN file $inFile";

	while (<IN>) {
		chomp;
		my $line = $_;
		if ( $line =~ m/<(\w+) name="(\w+)">([^<]+)</ ) {
			my $tagName = $1;
			my $idName = $2;
			my $stringValue = $3;
		
			if ( $tagName eq $expectedTag ) {
				print OUT sprintf("\t\tpublic static final int %s = 0x%x;\n", $idName, $currId);
			}
			$currId++;
		}
	}
	print OUT "\t}\n\n";
}