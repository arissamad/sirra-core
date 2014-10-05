if [ -z $1 ]; then
  echo You need a commit message.
  exit 1
fi

echo Commit message: $1

echo -e "\033[1m--Incrementing version\033[0m"
mvn autoincrement-versions:increment

echo -e "\033[1m--Committing to Github\033[0m"
git add .
git add -u
git commit -m "$1"
git push

echo -e "\033[1m--Deploying\033[0m"
mvn deploy